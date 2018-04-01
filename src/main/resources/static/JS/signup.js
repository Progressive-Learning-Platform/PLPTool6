$(document).ready(function(){

    populateCalender();

    $("#profileImage").attr("src",localStorage.ImageURL);

    var item = localStorage.editProfileFlag;
    alert("item "+ item);
    if(item == 0){
       $('.signUpLabel').text("PROGRESSIVE LEARNING PLATFORM - SIGN UP");
       $('.saveUser').val("Submit");
    }else if(item == 1){
       $('.signUpLabel').text("EDIT PROFILE");
       $('.saveUser').val("Update");
       getUserProfile();
    }else{
       $('.signUpLabel').text("PLEASE COMPLETE YOUR PROFILE");
       $('.saveUser').val("Update");
       getUserProfile();
    }
});



function getCookie(name) {
  var regexp = new RegExp("(?:^" + name + "|;\s*"+ name + ")=(.*?)(?:;|$)", "g");
  var result = regexp.exec(document.cookie);
  return (result === null) ? null : result[1];
}

function registerUser(){

    var formData = JSON.stringify({
                        "name": $('input[name=name]').val(),
                        "email": $('input[name=emailid]').val(),
                        "org_school": $('input[name=college]').val(),
                        "gender": $('input[name=gender]').val(),
                        "dateOfBirth": $('input[name=mm]').val() + $('input[name=dd]').val() + $('input[name=yyyy]').val(),
                        "contact_no": $('input[name=contactnum]').val(),
                        "alt_no": $('input[name=contactnum2]').val(),
                        "profile_photo": $('input[name=file_nm]').val(),
                        "password": $('input[name=password]').val()
                 });

      if(validateUserForm())
      {
        alert("Form validation done");
        $.ajax
         ({
           type: "POST",
           url: "http://localhost:8080/registerUser",
           contentType: 'application/json; charset=utf-8',
           headers: {'X-XSRF-TOKEN':getCookie("XSRF-TOKEN")},
           crossDomain: true,
           data: formData,
           success: function(response){
                     alert("alert "+ response);
                    if(JSON.parse(response).status == "success"){
                                               localStorage.Email =  $('.email').val();
                                                window.location.href = 'signup.html';
                                           }

            }
         });
        }

    }


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


function signOut(){
     localStorage.removeItem("Name");
     localStorage.removeItem("ImageURL");
     localStorage.removeItem("Email");
     window.location = "https://mail.google.com/mail/u/0/?logout&hl=en";
     window.location.href = 'index.html';
     var cookies = $.cookie();
     for(var cookie in cookies) {
        $.removeCookie(cookie);
     }


}


function validateUserForm()
{
 if(document.signup.emailid.value == "" )
 {
    $(".errorMsg").text("Please enter a valid email address");
    $('#errorModel').modal({
                          show: true,
                          backdrop: 'static',
                          keyboard: true,
                          });
    return false;
 }


 if(!document.signup.password.disabled && document.signup.password.value == "" )
 {
    $(".errorMsg").text("Please enter a valid password");
    $('#errorModel').modal({
                          show: true,
                          backdrop: 'static',
                          keyboard: true,
                          });
    return false;
 }

  if(!document.signup.cpassword.disabled && document.signup.cpassword.value == "" )
 {
     $(".errorMsg").text("Please enter a valid confirm password");
    $('#errorModel').modal({
                          show: true,
                          backdrop: 'static',
                          keyboard: true,
                          });
    return false;
 }

  if(document.signup.name.value == "" )
 {
    $(".errorMsg").text("Please enter your Full Name");
    $('#errorModel').modal({
                          show: true,
                          backdrop: 'static',
                          keyboard: true,
                          });
    return false;
 }

 if(document.signup.contactnum.value == "")
 {
     $(".errorMsg").text("Please enter your contact number");
    $('#errorModel').modal({
                          show: true,
                          backdrop: 'static',
                          keyboard: true,
                          });
    return false;
 }
 else if(telephoneCheck(document.signup.contactnum.value)){
     $(".errorMsg").text("Please enter a valid contact number");
        $('#errorModel').modal({
                              show: true,
                              backdrop: 'static',
                              keyboard: true,
                              });
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
    return false;
 }

 return true ;
}

function agreement(){
    $('#termsAndConditions').modal({
                              show: true,
                              backdrop: 'static',
                              keyboard: true,
                              });
}

function getUserProfile(){
    $.ajax
       ({
         type: "GET",
         url: "http://localhost:8080/getUserInfo?email="+localStorage.Email,
         contentType: 'application/json; charset=utf-8',
         headers: {'X-XSRF-TOKEN':getCookie("XSRF-TOKEN")},
         crossDomain: true,
         success: function(response){
                   console.log(response);
                   var res = JSON.parse(response);
                   console.log(res);
                   //if(res.status == true){
                       $('.email').val(localStorage.Email);
                       $('.fullName').val(res.name);
                       $('.org_school').val(res.org_school);
                        $('.dateOfBirth').val(res.dateOfBirth);
                        $('.contact_no').val(res.contact_no);
                        $('.alt_no').val(res.alt_no);
                        $(".password").attr("disabled", "disabled");
                        $(".cpassword").attr("disabled", "disabled");
                        if(res.gender == "1"){
                           $('.male').attr('checked', true);
                        }else{
                            $('.female').attr('checked', true);
                        }
                   //}

          }

       });
}


function populateCalender(){

    //populate our years select box
        for (i = new Date().getFullYear(); i > 1900; i--){
            $('#years').append($('<option />').val(i).html(i));
        }
        //populate our months select box
        for (i = 1; i < 13; i++){
            $('#months').append($('<option />').val(i).html(i));
        }
        //populate our Days select box
        updateNumberOfDays();

        //"listen" for change events
        $('#years, #months').change(function(){
            updateNumberOfDays();
        });
}

//function to update the days based on the current values of month and year
function updateNumberOfDays(){
    $('#days').html('');
    month = $('#months').val();
    year = $('#years').val();
    days = daysInMonth(month, year);

    for(i=1; i < days+1 ; i++){
            $('#days').append($('<option />').val(i).html(i));
    }
}

//helper function
function daysInMonth(month, year) {
    return new Date(year, month, 0).getDate();
}

function saveAndUpdateUser(){
      var item = localStorage.editProfileFlag;
      alert("saveAndUpdateUser "+ item);
        if(item == 0){
            registerUser();
        }else{
             updateUser();
        }
}

function updateUser(){

    var dob = $("#days").val() + $("#months").val() + $("#years").val();

    var myRadio = $('input[name=gender]:checked', '#signup').val();

    var formData = JSON.stringify({
                    "name": $('input[name=name]').val(),
                    "newEmail": $('input[name=emailid]').val(),
                    "oldEmail": localStorage.Email,
                    "org_school": $('input[name=college]').val(),
                    "gender": myRadio,
                    "dateOfBirth": null,
                    "contact_no": $('input[name=contactnum]').val(),
                    "alt_no": $('input[name=contactnum2]').val(),
                    "profile_photo": $('input[name=file_nm]').val(),
                    "password": $('input[name=password]').val()
                   });


        if(validateUserForm())
        {
          $.ajax
           ({
             type: "POST",
             url: "http://localhost:8080/updateUser",
             contentType: 'application/json; charset=utf-8',
             headers: {'X-XSRF-TOKEN':getCookie("XSRF-TOKEN")},
             crossDomain: true,
             data: formData,
             success: function(response){
                        alert("update user "+JSON.parse(response).status)
                       if(JSON.parse(response).status == "success"){
                           localStorage.Email =  $('.email').val();
                            window.location.href = 'signup.html';
                       }

              }
           });
          }

}

function telephoneCheck(str) {
  var isphone = /^(1\s|1|)?((\(\d{3}\))|\d{3})(\-|\s)?(\d{3})(\-|\s)?(\d{4})$/.test(str);
  alert(isphone);
}