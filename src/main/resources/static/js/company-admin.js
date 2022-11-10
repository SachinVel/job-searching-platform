const Company = new function(){
    this.getProfile = function(){
        $.ajax({
          type:'GET',
          url : '/company/profile',
          success : function(response){
            $('#main-body').html(response);
          }
        });
      }

      this.updateProfile = function(){
        let formElem =$('#profile-form');
        let formData = new FormData(formElem[0]);
        $.post({
          url : '/company/profile',
          data : formData,
          processData: false,
          contentType: false,
          success : function(response){
            $('#main-body').html(response);
          }
        });
      }

      this.getJobs = function(){
        $.ajax({
          type:'GET',
          url : '/company/job',
          success : function(response){
            $('#main-body').html(response);
          }
        });
      }

      this.addJobs = function(){
        $.ajax({
          type:'GET',
          url : '/company/job',
          success : function(response){
            $('#main-body').html(response);
          }
        });
      }

      this.showJobForm = function(){
        $.ajax({
          type:'GET',
          url : '/company/job/add',
          success : function(response){
            $('#main-body').html(response);
          }
        });
      }

      this.addJobQuestion = function(){
        let quesitonElem = $('  <div>'+
            '<div>Question</div>'+
            '<button class="js-remove-job-question-btn">Remove</button>'+
            '<input type="text"  name="job-question" class="js-job-question"/>'+
        '</div>');

        $('#job-question-container').append(quesitonElem);
      }

      this.saveJob = function(){

        let formElem =$('#job-form');
        let formData = new FormData(formElem[0]);
        let questions = [];
        $('.js-job-question').each(function( index ) {
          console.log('question : ',$(this).val());
          if( $(this).val() ){
            questions.push($(this).val());
          }
        });

        formData.set('questions',questions);

        const queryString = new URLSearchParams(formData).toString();

        $.post({
          url : '/company/job/add',
          headers: {'Content-Type': 'application/x-www-form-urlencoded'},
          data : queryString,
          processData: false,
          contentType: false,
          success : function(response){
            $('#main-body').html(response);
          }
        });
      }

      this.updateJob = function(jobId){
        let formElem =$('#job-form');
        let formData = new FormData(formElem[0]);
        let questions = [];
        $('.js-job-question').each(function( index ) {
          if( $(this).val() ){
            questions.push($(this).val());
          }
        });

        formData.set('questions',questions);
        formData.set('jobId',jobId);

        const queryString = new URLSearchParams(formData).toString();

        $.post({
          url : '/company/job/update',
          headers: {'Content-Type': 'application/x-www-form-urlencoded'},
          data : queryString,
          processData: false,
          contentType: false,
          success : function(response){
            $('#main-body').html(response);
            $("#save-job-btn").text("update");
            $("#save-job-btn").attr("id", "update-job-btn");
          }
        });
      }

      this.deleteJob = function(jobId){
        $.ajax({
          type : 'DELETE',
          url : '/company/job?jobId='+jobId,
          success : function(response){
            $('#main-body').html(response);
          }
        });
      }

      this.viewJobDetails = function(jobId){
        $.ajax({
          type : 'GET',
          url : '/company/job/'+jobId,
          success : function(response){
            $('#main-body').html(response);
            $("#save-job-btn").text("update");
            $("#save-job-btn").attr("id", "update-job-btn");
          }
        });
      }

      this.viewJobApplication = function(jobId){
        $.ajax({
          type : 'GET',
          url : '/company/job/application/'+jobId,
          success : function(response){
            $('#main-body').html(response);
          }
        });
      }



      this.init = function(){

        $('#profile-menu').on('click',function(){
          Company.getProfile();
        });

        $('#view-job-menu').on('click',function(){
          Company.getJobs();
        });

        $('#add-job-menu').on('click',function(){
          Company.showJobForm();
        });

        //profile section

        $('#main-body').on('click', '#update-profile-btn', function(){
          Company.updateProfile();
        });

        $('#main-body').on('click', '#reset-profile-btn', function(){
          Company.getProfile();
        });

        //job section

        $('#main-body').on('click', '#add-job-question-btn', function(){
          Company.addJobQuestion();
        });

        $('#main-body').on('click', '.js-remove-job-question-btn', function(){
          $(this).closest('div').remove();
        });

        $('#main-body').on('click', '#save-job-btn', function(){
          Company.saveJob();
        });

        $('#main-body').on('click', '#update-job-btn', function(){
          let jobId = $('#job-id').val();
          Company.updateJob(jobId);
        });

        $('#main-body').on('click', '.js-delete-job-btn', function(){
          let jobId = $(this).closest('td').siblings('.js-job-id').text();
          Company.deleteJob(jobId);
        });

        $('#main-body').on('click', '.js-view-job-btn', function(){
          let jobId = $(this).closest('td').siblings('.js-job-id').text();
          Company.viewJobDetails(jobId);
        });

        $('#main-body').on('click', '.js-view-job-application-btn', function(){
          let jobId = $(this).closest('td').siblings('.js-job-id').text();
          Company.viewJobApplication(jobId);
        });

        

        

      }
}