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

  this.logout = function(){
    $.ajax({
      type: 'POST',
      url: '/logout',
      headers: { 'X-XSRF-TOKEN': csrfToken },
      success: function (response) {
        window.location.href="/login?logout";
      }
    });
  }

  this.withdrawApplication = function(jobId){
    $.ajax({
      type: 'DELETE',
      headers : {'X-XSRF-TOKEN': csrfToken},
      url: '/seeker/application/'+jobId,
      success: function (response) {
        $('#main-body').html(response);
      }
    });
  }


  this.init = function () {

    this.getCSRFToken();

    window.addEventListener('hashchange', function(){
      let hash = window.location.hash;
      hash = hash.replace('#','');
      $('#'+hash+'-menu' ).siblings('.side-bar-option').removeClass('side-bar-option-selected');
      $('#'+hash+'-menu' ).addClass('side-bar-option-selected');
      $( '#'+hash+"-menu" ).trigger( "click" );
    });

    
    $('#main-body').on('click', '.js-refresh-btn', function () {
      window.location.reload();
    });

    $('#main-body').on('keyup', '#job-search-input', function () {
      var value = $(this).val().toLowerCase();
      $("#job-table tr.table-entry").filter(function() {
        $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
      });
    });

    $('#profile-menu').on('click', function () {
      window.location.hash = 'profile';
      $( this ).siblings('.side-bar-option').removeClass('side-bar-option-selected');
      $( this ).addClass('side-bar-option-selected');
      Seeker.getProfile();
    });

    $('#document-menu').on('click', function () {
      window.location.hash = 'document';
      $( this ).siblings('.side-bar-option').removeClass('side-bar-option-selected');
      $( this ).addClass('side-bar-option-selected');
      Seeker.getDocument();
    });

    $('#job-menu').on('click', function () {
      window.location.hash = 'job';
      $( this ).siblings('.side-bar-option').removeClass('side-bar-option-selected');
      $( this ).addClass('side-bar-option-selected');
      Seeker.getJob();
    });

    $('#job-application-menu').on('click', function () {
      window.location.hash = 'job-application';
      $( this ).siblings('.side-bar-option').removeClass('side-bar-option-selected');
      $( this ).addClass('side-bar-option-selected');
      Seeker.getJobApplications();
    });

    $('#logout-btn').on('click', function () {
      Seeker.logout();
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

    $('#main-body').on('click', '.js-withdraw-application-btn', function () {
      let appId = $(this).closest('td').siblings('.js-app-id').text();
      Seeker.withdrawApplication(appId);
    });


    let hash = window.location.hash;
    hash = hash.replace("#","");
    if( hash!=null ){
      $( '#'+hash+"-menu" ).addClass('side-bar-option-selected');
      $( '#'+hash+"-menu" ).trigger( "click" );
    } 

  }
}
$(window).on('load', function(){
  Seeker.init();
});