const Seeker = new function () {

  let applicationJobId;
  let csrfToken;

  this.getCSRFToken = function () {
    csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');
  }

  this.getProfile = function () {
    $.ajax({
      type: 'GET',
      url: '/seeker/profile',
      success: function (response) {
        $('#main-body').html(response);
      }
    });
  }

  this.updateProfile = function () {
    let formElem = $('#profile-form');
    let formData = new FormData(formElem[0]);
    $.post({
      url: '/seeker/profile',
      data: formData,
      headers: { 'X-XSRF-TOKEN': csrfToken },
      processData: false,
      contentType: false,
      success: function (response) {
        $('#main-body').html(response);
      }
    });
  }



  this.getJob = function () {
    $.ajax({
      type: 'GET',
      url: '/seeker/job',
      success: function (response) {
        $('#main-body').html(response);
        $('#add-document-container').hide();
      }
    });
  }

  this.getJobDetails = function (jobId) {
    $.ajax({
      type: 'GET',
      url: '/seeker/job/' + jobId,
      success: function (response) {
        $('#main-body').html(response);
      }
    });
  }

  this.getDocument = function () {
    $.ajax({
      type: 'GET',
      url: '/seeker/document',
      success: function (response) {
        $('#main-body').html(response);
        $('#add-document-container').hide();
      }
    });
  }

  this.uploadDocument = function () {
    let formElem = $('#upload-document-form');
    let formData = new FormData(formElem[0]);
    $.post({
      url: '/seeker/document/upload',
      headers: { 'X-XSRF-TOKEN': csrfToken },
      data: formData,
      processData: false,
      contentType: false,
      success: function (response) {
        $('#main-body').html(response);
      }
    });
  }

  this.deleteDocument = function (docId) {

    $.ajax({
      type: 'DELETE',
      url: '/seeker/document?docId=' + docId,
      headers: { 'X-XSRF-TOKEN': csrfToken },
      success: function (response) {
        $('#main-body').html(response);
      }
    });
  }

  this.startJobApplication = function (jobId) {
    $.ajax({
      type: 'GET',
      url: '/seeker/application/' + jobId,
      success: function (response) {
        $('#main-body').html(response);
        if ($('.js-question-container').length == 0) {
          $('#question-section').hide();
          if ($('#resume-input').length == 0 && $('#cover-letter-input').length == 0) {
            $('#document-section').hide();
            $('#complete-section').show();
          }
        } else {
          $('#document-section').hide();
        }
      }
    });
  }

  this.submitJobApplication = function () {
    let formData = new FormData();
    formData.set('jobId', applicationJobId);
    formData.set('resumeId', $('#resume-input').val());
    formData.set('coverLetterId', $('#cover-letter-input').val());
    let answers = [];
    $('.js-question-container').each(function () {
      let answer = {
        questionId: $(this).find('.js-question-id').text(),
        value: $(this).find('.js-answer-value').val()
      }
      answers.push(answer);
    });
    formData.set('answers', JSON.stringify(answers));

    const queryString = new URLSearchParams(formData).toString();

    $.post({
      url: '/seeker/application',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded','X-XSRF-TOKEN': csrfToken },
      data: queryString,
      processData: false,
      contentType: false,
      success: function (response) {
        $('#main-body').html(response);
      }
    });
  }

  this.getJobApplications = function () {
    $.ajax({
      type: 'GET',
      url: '/seeker/application',
      success: function (response) {
        $('#main-body').html(response);
      }
    });
  }


  this.init = function () {

    this.getCSRFToken();

    $('#profile-menu').on('click', function () {
      Seeker.getProfile();
    });

    $('#document-menu').on('click', function () {
      Seeker.getDocument();
    });

    $('#job-menu').on('click', function () {
      Seeker.getJob();
    });

    $('#job-application-menu').on('click', function () {
      Seeker.getJobApplications();
    });

    //job
    $('#main-body').on('click', '.js-view-job-btn', function () {
      let jobId = $(this).closest('td').siblings('.js-job-id').text();
      Seeker.getJobDetails(jobId);
    });

    $('#main-body').on('click', '#create-job-application-btn', function () {
      let jobId = $('#job-id').text();
      applicationJobId = jobId;
      Seeker.startJobApplication(jobId);
    });

    $('#main-body').on('click', '#application-question-next-btn', function () {
      $('#question-section').hide();
      if ($('#resume-input').length == 0 && $('#cover-letter-input').length == 0) {
        $('#document-section').hide();
        $('#complete-section').show();
      } else {
        $('#document-section').show();
        $('#complete-section').hide();
      }
    });

    $('#main-body').on('click', '#application-document-next-btn', function () {
      $('#document-section').hide();
      $('#complete-section').show();
    });

    $('#main-body').on('click', '#application-submit-btn', function () {
      Seeker.submitJobApplication();
    });



    //profile section
    $('#main-body').on('click', '#update-profile-btn', function () {
      Seeker.updateProfile();
    });

    $('#main-body').on('click', '#reset-profile-btn', function () {
      Seeker.getProfile();
    });

    //document section
    $('#main-body').on('click', '#add-document-btn', function () {
      $('#add-document-container').show();
    });

    $('#main-body').on('click', '#upload-document-btn', function () {
      Seeker.uploadDocument();
    });

    $('#main-body').on('click', '.js-delete-btn', function () {
      let docId = $(this).closest('td').siblings('.js-doc-id').text();
      Seeker.deleteDocument(docId);
    });

  }
}
$(window).on('load', function(){
  Seeker.init();
});