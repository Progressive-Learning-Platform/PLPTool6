
  $(document).ready(function ()
        {
         $("#profileImage").attr("src",localStorage.ImageURL);
            var rss = (function ($) {
                var ht = $(window).height()*0.82;
                console.log(localStorage);
                var createWidgets = function () {
                    $('#mainSplitter').jqxSplitter({ width: '100%', height: ht, orientation: 'horizontal', panels: [{ size: '80%', collapsible: false }] });
                    $('#firstNested').jqxSplitter({ width: '100%', height: '100%',  orientation: 'vertical', panels: [{ size: '12.5%', collapsible: false}] });
                    $('#secondNested').jqxSplitter({ width: '100%', height: '100%',  orientation: 'horizontal', panels: [{ size: '60%'}] });
                    $('#thirdNested').jqxSplitter({ width: '100%', height: '100%',  orientation: 'vertical', panels: [{ size: '50%'}] });
                    $('#fourthNested').jqxSplitter({ width: '100%', height: '100%',  orientation: 'horizontal', panels: [{ size: 150, collapsible: false}] });
                };
                return {
                    init: function () {
                        createWidgets();
                    }
                }

            } (jQuery));
            rss.init();
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
<<<<<<< HEAD
    alert("homepage logout");
    var auth2 = gapi.auth2.getAuthInstance();
=======
    var auth2 = gapi.auth2.getAuthInstance();
    localStorage["auth"] = auth2;
>>>>>>> origin/fixing-issue-126
    auth2.signOut().then(function () {
        console.log('User signed out.');
        localStorage.removeItem("Name");
        localStorage.removeItem("ImageURL");
        localStorage.removeItem("Email");
        localStorage.removeItem("editProfileFlag");
        window.location.href = 'index.html';
    });

}

function editProfile(){
<<<<<<< HEAD
    localStorage.editProfileFlag = 1;
=======
    localStorage.editProfileFlag = 0;
>>>>>>> origin/fixing-issue-126
    window.location.href = 'signup.html';
}





