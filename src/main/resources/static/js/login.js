$(window).on('load', function () {
     let csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');
    $('#csrf-token').val(csrfToken);
});