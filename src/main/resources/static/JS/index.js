//localStorage.editProfileFlag = 0 sign up link
//localStorage.editProfileFlag = 1 edit profile button
//localStorage.editProfileFlag = 2 all other
localStorage.token = getCookie("XSRF-TOKEN");
localStorage.editProfileFlag = 2;

 window.onbeforeunload = function(e){
      gapi.auth2.getAuthInstance().signOut();
    };

function getCookie(name) {
    var regexp = new RegExp("(?:^" + name + "|;\s*"+ name + ")=(.*?)(?:;|$)", "g");
    var result = regexp.exec(document.cookie);
    return (result === null) ? null : result[1].trim();
}


// user authentication and cookie information

function SignIn(){
    var email = $('input[name=emailid]').val().trim();
    var password = $('input[name=pwd]').val().trim();
    if(validateFields())
    {
      // user authentication and cookie information
        $.ajax
        ({
          type: "POST",
          url: "http://localhost:8080/authenticateUser",
          contentType: 'application/json; charset=utf-8',
          headers: {'X-XSRF-TOKEN':getCookie("XSRF-TOKEN")},
          crossDomain: true,
          data: JSON.stringify({
                	"email": email,
                	"password": password
                }),
          success: function(response){
                    if(JSON.parse(response).status == "true"){
                        localStorage.Email = email;
                        getProfile();
                    }
                    else{
                     $(".errorMsg").text("Incorrect Email address/ Password");
                                          $('#errorModel').modal({
                                                                show: true,
                                                                backdrop: 'static',
                                                                keyboard: true,
                                                                });
            }
           }
        });
    }
    else{

        $(".errorMsg").text("Incorrect Email address/ Password");
                                                  $('#errorModel').modal({
                                                                        show: true,
                                                                        backdrop: 'static',
                                                                        keyboard: true,
                                                                        });

    }




}

function validateFields(){

    if($('.emailid').val() == "" )
     {
        $(".errorMsg").text("Please enter a valid email address");
        $('#errorModel').modal({
                              show: true,
                              backdrop: 'static',
                              keyboard: true,
                              });
        return false;
     }

     else if($('.password').val() == "")
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

// check if profile is complete/incomplete
function getProfile(){
    $.ajax
       ({
         type: "GET",
         url: "http://localhost:8080/getUserInfo?email="+localStorage.Email,
         contentType: 'application/json; charset=utf-8',
         headers: {'X-XSRF-TOKEN':getCookie("XSRF-TOKEN")},
         crossDomain: true,
         success: function(response){
                   if (typeof response.status !== "undefined") {// no user info found
                       window.location.href = 'signup.html';
                   }
                   else{
                       window.location.href = 'signup.html';
                   }
          }
       });
}

function signup(){
    localStorage.editProfileFlag = 0;
    window.location.href="signup.html";
}




(function () {
      var webAuth = new auth0.WebAuth({
        domain: 'web-plp.auth0.com',
        clientID: '6HqpDmT0wKNYUGTiMiiNN8jH21oN0GYj',
        redirectUri: 'http://localhost:8080/plp/static/index.html',
        responseType: 'id_token',
        scope: 'openid profile'
      });

      var loginBtn = document.getElementById('yahoo-btn-login');
      loginBtn.addEventListener('click', function (e) {
        e.preventDefault();
        webAuth.authorize();
      });

      function handleAuthentication() {
        webAuth.parseHash(function (err, authResult) {
          if (authResult && authResult.idTokenPayload) {
            window.location.hash = '';
            <!--alert('your user_id is: ' + authResult.idTokenPayload.name);-->
            localStorage.Name = authResult.idTokenPayload.name;
            localStorage.ImageURL = authResult.idTokenPayload.picture;
            localStorage.Email = authResult.idTokenPayload.name;
            localStorage.editProfileFlag = 2;
            saveUser();
            window.location.href = 'signup.html';

          }
        });
      }

      //handleAuthentication();

   })();



// gmail login

  function onSignIn(googleUser)
  {
      var profile = googleUser.getBasicProfile();
       console.log(profile);
      if(profile!=null)
      {
//          window.location.href = 'signup.html';
          localStorage.Name = profile.getName();
          localStorage.ImageURL = profile.getImageUrl();
          localStorage.Email = profile.getEmail();
          localStorage.editProfileFlag = 2;
          window.location.href = 'signup.html';

      }
      else{
         $(".errorMsg").text("Error signing in through Google. Please try again later");
        $('#errorModel').modal({
                        show: true,
                        backdrop: 'static',
                        keyboard: true,
                        });
      }


  }

  function signOut() {
            var auth2 = gapi.auth2.getAuthInstance();
            auth2.signOut().then(function ()
            {
                localStorage.removeItem("Name");
                localStorage.removeItem("ImageURL");
                localStorage.removeItem("Email");
            });
    }
