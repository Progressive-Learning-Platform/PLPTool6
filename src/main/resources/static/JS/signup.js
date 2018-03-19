//Form validation code will come here.

$(document).ready(function(){
    var item = localStorage.editProfileFlag;
    if(item == 0){
        $('.signUpLabel').text("EDIT PROFILE");
        $('.submitBtn').val('Submit');

    }else if(item == 1){
        $('.signUpLabel').text("PLEASE COMPLETE YOUR PROFILE");
    }
});

$(document).ready(function (){
         $("#profileImage").attr("src",localStorage.ImageURL);

});

// Log out menu

function dropDownToggle() {
        document.getElementById("myDropdown").classList.toggle("show");
    }

// Close the dropdown if the user clicks outside of it
window.onclick = function(event) {
  if (!event.target.matches('.dropbtn')) {

    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains('show')) {
        openDropdown.classList.remove('show');
      }
    }
  }
}

function logout(){
     localStorage.removeItem("Name");
     localStorage.removeItem("ImageURL");
     localStorage.removeItem("Email");
     localStorage.removeItem("editProfileFlag");
     window.location = "https://mail.google.com/mail/u/0/?logout&hl=en";
     window.location.href = 'index.html';


}


function validate()
{
 if(document.signup.emailid.value == "" )
 {
    $(".errorMsg").text("Please enter a valid email address");
    $('#errorModel').modal({
                          show: true,
                          backdrop: 'static',
                          keyboard: true,
                          });
    <!--document.myForm.emailid.focus() ;-->
    return false;
 }

 if(document.signup.password.value == "" )
 {
    $(".errorMsg").text("Please enter a valid password");
    $('#errorModel').modal({
                          show: true,
                          backdrop: 'static',
                          keyboard: true,
                          });
    <!--document.myForm.emailid.focus() ;-->
    return false;
 }

  if(document.signup.cpassword.value == "" )
 {
     $(".errorMsg").text("Please enter a valid confirm password");
    $('#errorModel').modal({
                          show: true,
                          backdrop: 'static',
                          keyboard: true,
                          });
    <!--document.myForm.emailid.focus() ;-->
    return false;
 }

  if(document.signup.name.value == "" )
 {
    $(".errorMsg").text("Please enter your fullname");
    $('#errorModel').modal({
                          show: true,
                          backdrop: 'static',
                          keyboard: true,
                          });
    <!--document.myForm.emailid.focus() ;-->
    return false;
 }


 <!--if( document.signup.Zip.value == "" ||-->
 <!--isNaN( document.signup.Zip.value ) ||-->
 <!--document.signup.Zip.value.length != 5 )-->
 <!--{-->
    <!--alert( "Please provide a zip in the format #####." );-->
    <!--document.signup.Zip.focus() ;-->
    <!--return false;-->
 <!--}-->

 if(document.signup.contactnum.value == " " )
 {
     $(".errorMsg").text("Please enter your contact number");
    $('#errorModel').modal({
                          show: true,
                          backdrop: 'static',
                          keyboard: true,
                          });
    <!--document.myForm.emailid.focus() ;-->
    return false;
 }

 if( document.signup.college.value == "" )
 {
     $(".errorMsg").text("Please enter your college/ organization");
    $('#errorModel').modal({
                          show: true,
                          backdrop: 'static',
                          keyboard: true,
                          });
    <!--document.myForm.emailid.focus() ;-->
    return false;
 }

 <!--if( document.signup.college.value == "-1" )-->
 <!--{-->
     <!--$(".errorMsg").text("Please enter your college/ organization");-->
    <!--$('#errorModel').modal({-->
                          <!--show: true,-->
                          <!--backdrop: 'static',-->
                          <!--keyboard: true,-->
                          <!--});-->
    <!--&lt;!&ndash;document.myForm.emailid.focus() ;&ndash;&gt;-->
    <!--return false;-->
 <!--}-->
 return( true );
}

