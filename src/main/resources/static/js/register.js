const Register = new function(){
   this.init = function(){
      if( $('#user-role').val().includes('COMPANY_ADMIN') ){
         $('#company-container').show();
      }else{
         $('#job-seeker-container').show();
      }
      $('#user-role').on('change',function(){
         $('.js-additional-details').hide();
         if( $(this).val().includes('COMPANY_ADMIN') ){
            $('#company-container').show();
         }else{
            $('#job-seeker-container').show();
         }
      });
   }
}