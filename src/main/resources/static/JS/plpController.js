var app = angular.module('PLP', ['ngCookies']);

app.config([ '$httpProvider', function($httpProvider) {
	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
} ]);

app.controller('idectrl', [ '$scope', '$cookies', '$http', function( $scope, $cookies, $http){

	var getUser = function() {
		$http.get('/user').success(function(user) {
			$scope.user = user;
			console.log('Logged User : ', user);
			var indata = {	firstName: user.userAuthentication.details.given_name,
					  	  	lastName: user.userAuthentication.details.family_name,
					  	  	email: user.userAuthentication.details.email }
			$http({
	            method: 'POST',
	            url: 'http://localhost:8080/saveUser',
	            headers: {'Content-Type': 'application/json'},
	            data: indata,
	        });
		}).error(function(error) {
			$scope.resource = error;
		});
	};
	getUser();

	// method for logout
	$scope.logout = function() {
		$http.post('/logout').success(function(res) {
			$scope.user = null;
		}).error(function(error) {
			console.log("Logout error : ", error);
		});
	};
	
    $(document).ready(function () {
        $("#tabswidget").jqxTabs({  height: '100%', width: '100%' });
        $("#tabswidget1").jqxTabs({  height: '100%', width: '100%' });
        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/tomorrow");
        editor.getSession().setMode("ace/mode/plp");

        var editor2 = ace.edit("editor2");
        editor2.setTheme("ace/theme/tomorrow");
        editor2.getSession().setMode("ace/mode/plp");
    });

    $scope.inFile = null;

    $scope.register = function() {
        $http.get("http://localhost:8080/register?un=abc")
            .then(function(response) {
                console.log("response for register is: "+response.data.status+"\t"+response.data.session_key);
                if(response.data.status == "failed") {
                    alert("Server Error\nRedirecting to Google.com");
                    window.location.href = "https://www.google.com/";
                } else {
                    /*
                     ctr = 1;
                     str = 'sessionKey'+ctr;
                     while($cookies.get(str)!=null){
                     ctr+=1;
                     str = 'sessionKey'+ctr;
                     }
                     */
                    var cname = "sessionKey";
                    $cookies.put(cname,response.data.session_key
                        , {path : '/',});
                }
                var sessKey1 = $cookies.get("sessionKey");
                console.log("register Session key: "  + sessKey1);


            });

    };

    $scope.uploadFile = function() {
        console.log("in uploadFile");
        fileIn = $('#upfile');
        fileIn.trigger('click');
        console.log("just before return");
        return false;
    };

    $scope.newFile = function() {
        console.log("in newFile");
        $("#fileTree ul").append('<li>FileName.asm</li>');
    };

    $scope.openFile = function() {
        console.log("in openFile");
        if ((window.File!=null) && (window.FileReader!=null) && (window.FileList!=null) && (window.Blob!=null)) {
            // do your stuff!
            console.log("in if");
            str = "Uploading ";
            fileIn = $('#upfile');
            var f = fileIn[0].files[0];
            consoletxt = document.getElementById('console');

            var formData = new FormData();
            formData.append("file", f);
            $http({
                method: 'POST',
                url: 'http://localhost:8080/uploadFile',
                headers: {'Content-Type': undefined},
                data: formData,
                transformRequest: function (data, headersGetterFunction) {
                    return data;
                }
            })
                .success(function (data, status){
                })
                .error(function(data, status) {
                });

            if (f) {
                var r = new FileReader();
                var textContent = r.result;
                r.onload = function(e) {
                    var contents = e.target.result;
                    alert( "Got the file.n"
                        +"name: " + f.name + "n"
                        +"type: " + f.type + "n"
                        +"size: " + f.size + " bytesn"
                        + "starts with: " + r.result
                        +"\n"+str
                    );
                    consoletxt.innerHTML = "\""+contents+"\"";
                    console.log(contents);
                    var editor = ace.edit("editor");
                    editor.setValue(r.result);
                }
                temp = r.readAsText(f);

            } else {
                alert("Failed to load file");
            }
        } else {
            alert('The File APIs are not fully supported by your browser.');
        }
    };

    $scope.assembleFile = function() {
        console.log("in Assembly");
        var code = [];
        
        var editor = ace.edit("editor");
        var codeText = editor.getValue();
        code.push(codeText);
        var editor2 = ace.edit("editor2");
        var codeText2 = editor2.getValue();
        if(codeText2 || 0 !== codeText2.length)
        	code.push(codeText2);
       
        if (!(/\S/.test(codeText))){
            alert("Please write some code before trying to assemble.")	;
            return;
        }
        console.log(codeText);

        //var jsonCode = JSON.parse(codeText);
        var sessKey = $cookies.get('sessionKey');
        console.log("assemble Session key: "  + sessKey);
        var indata = {code: code, sessionKey: sessKey }
        $http({
            method: 'POST',
            url: 'http://localhost:8080/assembleText',
            headers: {'Content-Type': 'application/json'},
            data: indata,


        })

            .then(function(response){
                console.log("Assemble Status:" + response.data.status)
                if(response.data.status == "ok"){
                    console.log("Success:" + JSON.stringify(response))
                    $('#consoleBox').children('span').text("Assemble Successful.");
                }
                else{
                    console.log("Error:" + response.data.message)
                    var errorMessage =  response.data.message;
                    $('#consoleBox').children('span').text(errorMessage);
                }

            });

    };

    $scope.SimulateFile = function() {
        console.log("in Simulate");
        var sessKey = $cookies.get('sessionKey');
        console.log("simulate Session key: "  + sessKey);
        
        $http({
            method: 'POST',
            url: 'http://localhost:8080/Simulator',
            headers: {'Content-Type': undefined},
            data: sessKey,
        })
            .then(function(response){
                console.log("Simulator Status:" + response.data.status)
                if(response.data.status == "ok"){
                    console.log("Success:" + JSON.stringify(response))
                    $('#consoleBox').children('span').text("Simulation Successful.");
                }
                else{
                    if(response.data.simError == "no-asm"){
                        var errorMessage =  "NO ASM IMAGE FOUND!!"
                        $('#consoleBox').children('span').text(errorMessage);
                        alert("Please successfully assemble the ASM file first.");
                    }
                    else if(response.data.simError == "no-sim"){
                        var errorMessage =  "NO SIMULATOR FOUND!!"
                        $('#consoleBox').children('span').text(errorMessage);
                        alert("No simulator found for this project type");
                    }
                }

            });

    };

    $scope.run = function() {
        console.log("runnnning");

        //var jsonCode = JSON.parse(codeText);

        $http({
            method: 'GET',
            url: 'http://localhost:8080/Run',
            headers: {'Content-Type': undefined},
        })
            .then(function(response){
                console.log("Running Status:" + response.data.status)
                if(response.data.status == "ok"){
                    console.log("Success:" + JSON.stringify(response))
                    $('#consoleBox').children('span').text("Running.");
                }
                else{
                    var errorMessage =  "Cannot run because project not simulated successfully!";
                    $('#consoleBox').children('span').text(errorMessage);
                    alert("Error!");
                }

            });


    };


    $scope.stopsimulation = function() {
        console.log("Stopping Simulation");

        $http({
            method: 'GET',
            url: 'http://localhost:8080/Stop',
            headers: {'Content-Type': undefined},
        })
            .then(function successCallback(response) {
                console.log("Success:" + JSON.stringify(response)) ;
                // this callback will be called asynchronously
                // when the response is available
            }, function errorCallback(response) {
                console.log("error");

                // called asynchronously if an error occurs
                // or server returns response with an error status.
            });


    };

    $scope.WatchReg = function() {
        console.log("Watch REgister func");

        $http({
            method: 'GET',
            url: 'http://localhost:8080/WatchReg',
            headers: {'Content-Type': undefined},
        })
            .then(function successCallback(response) {
                console.log("Success:" + JSON.stringify(response)) ;
                // this callback will be called asynchronously
                // when the response is available
                var val = $("#register").val();
                $('#registerTable tbody').append(
                    '<tr>' +
                    '   <td>' + val + '</td>' +
                    '   <td>' + 9 + '</td>' +
                    '   <td>' + 0 + '</td>' +
                    '</tr>');


            }, function errorCallback(response) {
                console.log("error");

                // called asynchronously if an error occurs
                // or server returns response with an error status.
            });


    };

    $scope.stepsimulation = function() {
        console.log("Step Simulation");

        $http({
            method: 'GET',
            url: 'http://localhost:8080/StepSim',
            headers: {'Content-Type': undefined},
        })
            .then(function successCallback(response) {
                console.log("Success:" + JSON.stringify(response)) ;
                // this callback will be called asynchronously
                // when the response is available
            }, function errorCallback(response) {
                console.log("error");

                // called asynchronously if an error occurs
                // or server returns response with an error status.
            });


    };


    $scope.openLED = function() {
        //console.log(Jquery.ui);
        //alert("okay");
        if(document.getElementById('led').checked) {
            $('#window').prepend($("<div class='draggable ui-widget-content' id='ledDisplay'>" +
                "   <h4>LED Array I/O</h4>"+
                "   <div class='row'>"+
                "       <div class='form-group'>"+
                "           <div class='col-xs-6'>"+
                "               <label for='address'>Address:</label>"+
                "               <input type='text' class='form-control' id='address' placeholder='0xf0200000' disabled/>"+
                "           </div>"+
                "           <div class='col-xs-6'>"+
                "               <label for='value'>Value:</label>"+
                "               <input type='text' class='form-control' id='value' placeholder='0x00000000' disabled/>"+
                "            </div>"+
                "        </div>"+
                "    </div>"+
                "   </br></br>"+
                "   <div class='row'> "+
                "       <div class='col-xs-2'> "+
                "           <label class='switchled'>"+
                "               <div class='led-box'>"+
                "                   <div class='led-black' id='led1'></div>"+
                "               </div>"+
                "           </label>"+
                "       </div>"+
                "       <div class='col-xs-2'> "+
                "           <label class='switchled'>"+
                "               <div class='led-box'>"+
                "                   <div class='led-black' id='led2'></div>"+
                "               </div>"+
                "           </label>"+
                "       </div>"+
                "       <div class='col-xs-2'> "+
                "           <label class='switchled'>"+
                "               <div class='led-box'>"+
                "                   <div class='led-black' id='led3'></div>"+
                "               </div>"+
                "           </label>"+
                "       </div>"+
                "       <div class='col-xs-2'> "+
                "           <label class='switchled'>"+
                "               <div class='led-box'>"+
                "                   <div class='led-black' id='led4'></div>"+
                "               </div>"+
                "           </label>"+
                "       </div>"+
                "       <div class='col-xs-2'> "+
                "           <label class='switchled'>"+
                "               <div class='led-box'>"+
                "                   <div class='led-black' id='led5'></div>"+
                "               </div>"+
                "           </label>"+
                "       </div>"+
                "       <div class='col-xs-2'> "+
                "           <label class='switchled'>"+
                "               <div class='led-box'>"+
                "                   <div class='led-black' id='led6'></div>"+
                "               </div>"+
                "           </label>"+
                "       </div>"+
                "       <div class='col-xs-2'> "+
                "           <label class='switchled'>"+
                "               <div class='led-box'>"+
                "                   <div class='led-black' id='led7'></div>"+
                "               </div>"+
                "           </label>"+
                "       </div>"+
                "       <div class='col-xs-2'> "+
                "           <label class='switchled'>"+
                "               <div class='led-box'>"+
                "                   <div class='led-black' id='led8'></div>"+
                "               </div>"+
                "           </label>"+
                "       </div>"+
                "    </div>"+
                " </div>", {
            }).draggable());
        } else {
            $('#ledDisplay').remove();
        }

    };

    $scope.openSwitches = function() {
        if(document.getElementById('switch').checked) {
            $('#window').prepend($(
                "<div class='draggable ui-widget-content' id='switches'>" +
                "   <h4>Switches</h4>"+
                "   <div class='row'>"+
                "       <div class='form-group'>"+
                "           <div class='col-xs-6'>"+
                "               <label for='address'>Address:</label>"+
                "               <input type='text' class='form-control' id='address' placeholder='0xf0200000'/>"+
                "           </div>"+
                "           <div class='col-xs-6'>"+
                "               <label for='value'>Value:</label>"+
                "               <input type='text' class='form-control' id='value' placeholder='0x00000000'/>"+
                "            </div>"+
                "        </div>"+
                "    </div>"+
                "    <br><br>"+
                "    <div class='row'>"+
                "        <div class='col-xs-2' style='transform: rotate(270deg);'>"+
                "            <div class='onoffswitch'>"+
                "                <input type='checkbox' name='onoffswitch' class='onoffswitch-checkbox' id='myonoffswitch'>"+
                "                <label class='onoffswitch-label' for='myonoffswitch'>"+
                "                    <span class='onoffswitch-inner'></span>"+
                "                    <span class='onoffswitch-switch'></span>"+
                "                </label>"+
                "            </div>"+
                "        </div>"+
                "        <div class='col-xs-2' style='transform: rotate(270deg);'>"+
                "            <div class='onoffswitch'>"+
                "                <input type='checkbox' name='onoffswitch' class='onoffswitch-checkbox' id='myonoffswitch1'>"+
                "                <label class='onoffswitch-label' for='myonoffswitch1'>"+
                "                    <span class='onoffswitch-inner'></span>"+
                "                    <span class='onoffswitch-switch'></span>"+
                "                </label>"+
                "            </div>"+
                "        </div>"+
                "        <div class='col-xs-2' style='transform: rotate(270deg);'>"+
                "            <div class='onoffswitch'>"+
                "                <input type='checkbox' name='onoffswitch' class='onoffswitch-checkbox' id='myonoffswitch2'>"+
                "                <label class='onoffswitch-label' for='myonoffswitch2'>"+
                "                    <span class='onoffswitch-inner'></span>"+
                "                    <span class='onoffswitch-switch'></span>"+
                "                </label>"+
                "            </div>"+
                "        </div>"+
                "        <div class='col-xs-2' style='transform: rotate(270deg);'>"+
                "            <div class='onoffswitch'>"+
                "                <input type='checkbox' name='onoffswitch' class='onoffswitch-checkbox' id='myonoffswitch3'>"+
                "                <label class='onoffswitch-label' for='myonoffswitch3'>"+
                "                    <span class='onoffswitch-inner'></span>"+
                "                    <span class='onoffswitch-switch'></span>"+
                "                </label>"+
                "            </div>"+
                "        </div>"+
                "        <div class='col-xs-2' style='transform: rotate(270deg);'>"+
                "            <div class='onoffswitch'>"+
                "                <input type='checkbox' name='onoffswitch' class='onoffswitch-checkbox' id='myonoffswitch4'>"+
                "                <label class='onoffswitch-label' for='myonoffswitch4'>"+
                "                    <span class='onoffswitch-inner'></span>"+
                "                    <span class='onoffswitch-switch'></span>"+
                "                </label>"+
                "            </div>"+
                "        </div>"+
                "        <div class='col-xs-2' style='transform: rotate(270deg);'>"+
                "            <div class='onoffswitch'>"+
                "                <input type='checkbox' name='onoffswitch' class='onoffswitch-checkbox' id='myonoffswitch5'>"+
                "                <label class='onoffswitch-label' for='myonoffswitch5'>"+
                "                    <span class='onoffswitch-inner'></span>"+
                "                    <span class='onoffswitch-switch'></span>"+
                "                </label>"+
                "            </div>"+
                "        </div>"+
                "        <div class='col-xs-2' style='transform: rotate(270deg);'>"+
                "            <div class='onoffswitch'>"+
                "                <input type='checkbox' name='onoffswitch' class='onoffswitch-checkbox' id='myonoffswitch6'>"+
                "                <label class='onoffswitch-label' for='myonoffswitch6'>"+
                "                    <span class='onoffswitch-inner'></span>"+
                "                    <span class='onoffswitch-switch'></span>"+
                "                </label>"+
                "            </div>"+
                "        </div>"+
                "        <div class='col-xs-2' style='transform: rotate(270deg);'>"+
                "            <div class='onoffswitch'>"+
                "                <input type='checkbox' name='onoffswitch' class='onoffswitch-checkbox' id='myonoffswitch7'>"+
                "                <label class='onoffswitch-label' for='myonoffswitch7'>"+
                "                    <span class='onoffswitch-inner'></span>"+
                "                    <span class='onoffswitch-switch'></span>"+
                "                </label>"+
                "            </div>"+
                "        </div>"+
                "    </div>"+
                " </div>", {
                }).draggable());
        } else {
            $('#switches').remove();
        }

    };

    $scope.open7Segment = function() {
        if(document.getElementById('7segment').checked) {
            $('#window').prepend($("<div class='draggable ui-widget-content' id='7segmentDisplay'>" +
                "   <h4>Seven Segment Display</h4>"+
                "   <div class='row'>"+
                "       <div class='form-group'>"+
                "           <div class='col-xs-6'>"+
                "               <label for='address'>Address:</label>"+
                "               <input type='text' class='form-control' id='address' placeholder='0xf0200000'/>"+
                "           </div>"+
                "           <div class='col-xs-6'>"+
                "               <label for='value'>Value:</label>"+
                "               <input type='text' class='form-control' id='value' placeholder='0x00000000'/>"+
                "            </div>"+
                "        </div>"+
                "    </div><br><br>"+
                // "   </br></br> <input type='text' id='valueText' />"+
                "   <div class='col-md-12' id='sevenSegmentArray'></div>" +
                " </div>", {
            }).draggable());
            $("#sevenSegmentArray").sevenSeg({ digits: 4 });
            // $("#valueText").keyup(function(){
            //     $("#sevenSegmentArray").sevenSeg({ value: this.value });
            // });
        } else {
            $('#7segmentDisplay').remove();
        }

    };

    $scope.openUART = function() {
        if(document.getElementById('uart').checked) {
            $('#window').prepend($("<div class='draggable ui-widget-content' id='uartDisplay'>" +
                "<img src='../images/uart.PNG'/>" +
                " </div>", {
            })).draggable();
        } else {
            $('#uartDisplay').remove();
        }

    };

    $scope.openVGA = function() {
        $('#window').prepend($("<div class='draggable ui-widget-content'>" +
            "<img src='../images/sevensegment.PNG'/>" +
            " </div>", {
        }).draggable());
    };

    $scope.openGPIO = function() {
        $('#window').prepend($("<div class='draggable ui-widget-content'>" +
            "   <h4>GPIO Module</h4>"+
            "   <label for='value'>Tristate Register Contents:</label>"+
            "   <input type='text' id='value' value='' disabled/>"+
            "   </br></br>"+
            "   <label class='switch'>"+
            "       <label>Port A:</label>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led1'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led2'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led3'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led4'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led5'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led6'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led7'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led8'></div>"+
            "       </div>"+
            "   </label>"+
            "  <br><br><hr><br><br>" +
            "   <label class='switch'>"+
            "       <label>Port B:</label>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led9'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led10'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led11'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led12'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led13'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led14'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led15'></div>"+
            "       </div>"+
            "   </label>"+
            "   <label class='switch'>"+
            "       <div class='led-box'>"+
            "           <div class='led-black' id='led16'></div>"+
            "       </div>"+
            "   </label>"+
            " </div>", {
        }).draggable());
    };

    var options = [];

    $( '.dropdown-menu a' ).on( 'click', function( event ) {

        var $target = $( event.currentTarget ),
            val = $target.attr( 'data-value' ),
            $inp = $target.find( 'input' ),
            idx;

        if ( ( idx = options.indexOf( val ) ) > -1 ) {
            options.splice( idx, 1 );
            setTimeout( function() { $inp.prop( 'checked', false ) }, 0);
        } else {
            options.push( val );
            setTimeout( function() { $inp.prop( 'checked', true ) }, 0);
        }

        $( event.target ).blur();

        console.log( options );
        return false;
    });

}]);
