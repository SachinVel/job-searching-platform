
const Register = new function(){
   this.getCSRFToken = function(){
      let csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');
      return csrfToken;
   }

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
$(window).on('load', function(){
   Register.init();
   let csrfToken = Register.getCSRFToken();
   $('#csrf-token').val(csrfToken);
});