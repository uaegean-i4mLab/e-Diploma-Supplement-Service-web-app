    $(document).ready(function () {
        console.log("login.js")
        // swellrt api 
        
        
        
        SwellRT.ready(function () {
            //login the user
            let token = $("#token").val();
            console.log("SwellRT is ready to use, token " + token);
            var eid = null;
            $.get("getEidFromToken?token=" + token,
                    function (data, status) {
                        console.log("Data: " + data + "\nStatus: " + status);
                        if (data.eid !== "NOT_FOUND") {
                            eid = data.eid;
                            loginOrRegisterAndLoginUser(eid, userLoggedInCallback, registerUserAndLoginUser);
                        } else {
                            alert("invalid token! Please login using Stork and try again");
                        }
                    });
        });
    });


    function loginOrRegisterAndLoginUser(eid, sucessCallback, failCallback) {
        SwellRT.login(
                {
                    id: eid + "@local.net",
                    password: "s8sjf72z!3"
                },
                function (response) {
                    if (response.error) {
                        // ERROR
                        console.log(response.error);
                        failCallback(eid, sucessCallback, undefined);
                    } else if (response.data) {
                        sucessCallback(eid);
                    }
                });
    }

    function registerUserAndLoginUser(eid, successCallback, failCallback) {
        SwellRT.createUser(
                {
                    id: eid + "@local.net",
                    password: "s8sjf72z!3"
                },
                function (res) {
                    if (res.error) {
                        if (res.error == "ACCOUNT_ALREADY_EXISTS") {

                        }
                        console.log(res.error);
                    } else if (res.data) {
                        successCallback(eid);
                    }
                });
    }


    function userLoggedInCallback(eid) {
        console.log("the user is logged in!! " + eid);
        //instantiate the editor
        instantiateTextEditor();
        //get all items of the logged in user
        getItemsOfLoggedUser().done(function (data) {
            console.log(data);
            let list = $("#itemsList");
            data.forEach(function (itemId) {
                list.append($('<li onclick=loadSwellDocument("'+itemId+'")>' + itemId + '</li>'));
            });
            displayMainContent();
        }).fail(function () {
            console.log("error getting items of user " + eid);
        });

    }

    function instantiateTextEditor() {
        var editor = SwellRT.editor("editor", null, null);
        // Open a collaborative object
        var co = SwellRT.open(
                {
                    // Leave this object empty to create a new object
                    //id: "local.net/s+cpY3mjyA6BA"
                },
                function (co) {
                    // Error
                    if (co == null) {
                        console.log("Error, object is null");
                    } else if (co.error) {
                        console.log("Error, " + co.error);
                    }
                    // Success
                    console.log("Object " + co.id() + " is ready!")
                    // Create a text field, then add it to the collab. object
                    var text = co.createText("Write here initial content");
                    text = co.root.put("text", text);
                    //Attach the text document to the editor with the method edit(TextType: text)
                    editor.edit(text);
                });
//

    }



    function getItemsOfLoggedUser() {
        let itemIds = [];
        var deferred = $.Deferred();

        SwellRT.query("{}",
                function (response) {
                    // response.result is the array of data modeles
//                    console.log(response);
                    response.result.forEach(function (item) {
                        itemIds.push(item.wave_id);
                    });
                    deferred.resolve(itemIds);
                },
                function (error) {
                    // handle the error
                    deferred.reject();
                });
        return deferred;
    }


function displayMainContent(){
    let mainDiv = $("#mainContent");
    let preloader = $("#preloader");
    preloader.hide();
    mainDiv.css("display","block");
}


function loadSwellDocument(eid){
    console.log("will load dociment" + eid);
}