//Form validation code will come here.
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