function SignIn(){

    if(validateFields())
    {
      // user authentication and cookie information

      //AJAX success
      window.location.href = 'homepage.html';

      //AJAX failure
       $(".errorMsg").text("Incorrect Email address/ Password");
                      $('#errorModel').modal({
                                            show: true,
                                            backdrop: 'static',
                                            keyboard: true,
                                            });
    }

}

function validateFields(){

    if($('#emailid').val() == "" )
     {
        $(".errorMsg").text("Please enter a valid email address");
        $('#errorModel').modal({
                              show: true,
                              backdrop: 'static',
                              keyboard: true,
                              });
        return false;
     }

     else if($('#pwd').val() == "")
     {
        $(".errorMsg").text("Please enter a valid password");
        $('#errorModel').modal({
                              show: true,
                              backdrop: 'static',
                              keyboard: true,
                              });
        return false;
     }
     return true;
}


var app = angular.module('PLP', ['ngCookies']);
//jQuery.noConflict();

app.config([ '$httpProvider', function($httpProvider) {
	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
} ]);

app.controller('idectrl', [ '$scope', '$cookies', '$http', function( $scope, $cookies, $http){

}]);
