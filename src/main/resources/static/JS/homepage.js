
  $(document).ready(function ()
        {
         $("#profileImage").attr("src",localStorage.ImageURL);
         $('#jqxTree').jqxTree({ height: '100%', width: '100%'});
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

        $(function () {
                    $('.tree li:has(ul)').addClass('parent_li').find(' > span').attr('title', 'Collapse this branch');

                    $('.tree li.parent_li > span').on('click', function (e) {
                        var children = $(this).parent('li.parent_li').find(' > ul > li');
                        if (children.is(":visible")) {
                            children.hide('fast');
                            $(this).attr('title', 'Expand').find(' > i').addClass('glyphicon-plus-sign').removeClass('glyphicon-minus-sign');
                        } else {
                            children.show('fast');
                            $(this).attr('title', 'Collapse').find(' > i').addClass('glyphicon-minus-sign').removeClass('glyphicon-plus-sign');
                        }
                        e.stopPropagation();
                    });

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
    var auth2 = gapi.auth2.getAuthInstance();
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
    localStorage.editProfileFlag = 1;
    window.location.href = 'signup.html';
}





