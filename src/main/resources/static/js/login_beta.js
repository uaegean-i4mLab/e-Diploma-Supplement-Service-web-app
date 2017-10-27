var s;

$(document).ready(function () {
    console.log("login_beta.js")
    // swellrt api 
//         s = swellrt.service.get();
    swellrt.onReady(function (service) {

        //login the user
        let token = $("#token").val();
        console.log("SwellRT is ready to use, token " + token);
        var eid = null;
        $.get("getEidFromToken?token=" + token,
                function (data, status) {
                    console.log("Data: " + data + "\nStatus: " + status);
                    if (data.eid !== "NOT_FOUND") {
                        eid = data.eid;
                        loginOrRegisterAndLoginUser(service, eid, userLoggedInCallback, registerUserAndLoginUser);
                    } else {
                        alert("invalid token! Please login using Stork and try again");
                    }
                });
    });
});



function loginOrRegisterAndLoginUser(swellrtService, eid, sucessCallback, failCallback) {
    console.log("will login user");
    swellrtService.login(
            {
                id: eid + "@local.net",
                password: "s8sjf72z!3"
            }
    ).then(response => {
        console.log("got the response ");
        console.log(response);
        sucessCallback(swellrtService, eid);
    }).catch(e => {
        console.log("Login error" + e);
        failCallback(swellrtService, eid, sucessCallback, undefined);
    });
}

function registerUserAndLoginUser(swellrtService, eid, successCallback, failCallback) {

    console.log("registering user then will login");
    swellrtService.createUser(
            {
                id: eid + "@local.net",
                password: "s8sjf72z!3"
            }
    ).then(res => {
        successCallback(swellrtService, eid);
    }).catch(e => {
        console.log("User creation error");
        console.log(e);
    });
}


function userLoggedInCallback(swellrtService, eid) {
    console.log("the user is logged in!! " + eid);
    //instantiate the editor
    instantiateTextEditor(swellrtService);
    //get all items of the logged in user
    getItemsOfLoggedUser(swellrtService).done(function (data) {
        console.log(data);
        let list = $("#itemsList");
        data.forEach(function (itemId) {
            list.append($('<li onclick=loadSwellDocument("' + itemId + '")>' + itemId + '</li>'));
        });
        displayMainContent();
    }).fail(function () {
        console.log("error getting items of user " + eid);
    });
}

function instantiateTextEditor(swellrtService) {
    let editor = swellrt.Editor.createWithId("editor", swellrtService);
    // Open a collaborative object
    var co = swellrtService.open(
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



function getItemsOfLoggedUser(swellrtService) {
    let itemIds = [];
    var deferred = $.Deferred();
    swellrtService.query("{}",
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


function displayMainContent() {
    let mainDiv = $("#mainContent");
    let preloader = $("#preloader");
    preloader.hide();
    mainDiv.css("display", "block");
}


function loadSwellDocument(eid) {
    console.log("will load dociment" + eid);
}