/**
 * Created by nitingoel on 7/30/16.
 */

var app = angular.module('PLP', ['ngCookies']);

app.controller('idectrl', [ '$scope', '$cookies', '$http', function( $scope, $cookies, $http){

    $scope.inFile = null;

    $scope.register = function() {
        $http.get("http://localhost:12345/register?un=abc")
            .then(function(response) {
                console.log("response for register is: "+response.data.status+"\t"+response.data.session_key);
                if(response.data.status == "failed") {
                    alert("Server Error\nRedirecting to Google.com");
                    window.location.href = "https://www.google.com/";
                } else {
                    ctr = 1;
                    str = 'sessionKey'+ctr;
                    while($cookies.get(str)!=null){
                        ctr+=1;
                        str = 'sessionKey'+ctr;
                    }
                    $cookies.put(str,response.data.session_key);
                }
            });

    };

    $scope.uploadFile = function() {
        console.log("in uploadFile");
        fileIn = $('#upfile');
        fileIn.trigger('click');
        console.log("just before return");
        return false;
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
                url: 'http://localhost:12345/uploadFile',
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
                r.onload = function(e) {
                    var contents = e.target.result;
                    alert( "Got the file.n"
                        +"name: " + f.name + "n"
                        +"type: " + f.type + "n"
                        +"size: " + f.size + " bytesn"
                        + "starts with: " + contents.substr(1, contents.indexOf("n"))
                        +"\n"+str
                    );
                    consoletxt.innerHTML = "\""+contents+"\"";
                    console.log(contents);
                }
                temp = r.readAsText(f);
            } else {
                alert("Failed to load file");
            }
        } else {
            alert('The File APIs are not fully supported by your browser.');
        }
    };

}]);
