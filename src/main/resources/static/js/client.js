// variables -----------------------------------------
var conn;
var peerConnection;
var inStream;
var countIceCandidate = 0;
var countCalls = 0;
var msgPoints = "...";
var msg="You have a call";
var key; //key for accessing to signaling server
var users; //names of users
var chats;
var remoteUser;
var localStream;
var foundUsers;
var ring = new Audio('../static/sound/ring.mp3');
ring.loop = true;
onloadPage();


//Message that something happens: connecting to the server, have a call
function createMsg(msg) {
    var line = document.getElementById("line");
    line.innerHTML = "<h3 id=\"msg\">"+msg+"</h3>";
}

function changeMsg(msg){
    document.getElementById("msg").remove();
    // setTimeout(function (){console.log('delete msg')},3000);
    createMsg(msg);
}
//-----------------------------------------------------------------------
//connecting to our signaling server

function createConnection(){
    conn = new WebSocket('ws://localhost:8081/socket');
    // conn = new WebSocket('wss://app-video-chat.herokuapp.com/socket');

    conn.onopen = function() {
        console.log("Connected to the signaling server");
        initialize();
        changeMsg("connected to the signaling server");
        sendLoginData();
        console.log("sendLoginData()");
    };

    conn.onclose = function () {
        console.log("Connection closed.");
    };

    conn.onmessage = function(msg) {
        console.log("Got message", msg.data);
        let content;
        let data;
        try{
            content = JSON.parse(msg.data);
            data = content.data;
        } catch (error){
            console.log("conn.onmessage() happened error: "+error);
            content = {event:"default",
                        data:"default"};
        }
        switch (content.event) {
            // when somebody wants to call us
            case "offer":
                handleOffer(data);
                break;
            case "answer":
                handleAnswer(data);
                break;
            // when a remote peer sends an ice candidate to us
            case "candidate":
                handleCandidate(data);
                break;
            case "call":
                youHaveCall(data);
                break;
            case "hangup":
                hangUp(data);
                break;
            case "message":
                handleMessage(data);
                break;
            case "key":
                handleKey(data);
                break;
            case "chats":
                handleChats(data);
                break;
            case "find":
                handleFind(data);
                break;
            default:
                chats = null;
                lightOffUsers();
                break;
        }
    };
}
//-----------------general functions---------------------------------------------

function send(message) {
    conn.send(JSON.stringify(message));
}

// peerConnection initialization ------------------------------------------------
function initialize() {
    let configuration = {
        'iceServers': [{
            'urls': ['stun:stun.l.google.com:19302' ]
        }]
    };

    peerConnection = new RTCPeerConnection(configuration,
        {
        optional : [ {
            RtpDataChannels : true
        } ]
    }
    );

    // Setup ice handling
    peerConnection.onicecandidate = function(event) {
        if (event.candidate) {
            send({
                event : "candidate",
                data : event.candidate,
            });
            countIceCandidate = countIceCandidate + 1;
            console.log("peerConnection.onicecandidate");
        }
    };
    peerConnection.ontrack = function (event) {
        inStream = new MediaStream();
        inStream.addTrack(event.track);
        document.getElementById("remoteVideo").srcObject = inStream;
        console.log('peerConnection.ontrack');
    };
}


function createOffer() {
    peerConnection.createOffer(function(offer) {
        send({
            event: "offer",
            data: offer,
        });
        peerConnection.setLocalDescription(offer);
    }, function(err) {
        console.log("createOffer() :"+err);
    });
    console.log("createOffer()");
}

// handlers--------------------------------------------------------------------
function handleOffer(offer) {
    peerConnection.setRemoteDescription(new RTCSessionDescription(offer));

    // create and send an answer to an offer
    peerConnection.createAnswer(function(answer) {
        peerConnection.setLocalDescription(answer);
        send({
            event : "answer",
            data : answer,
        });
    }, function() {
        console.log("handleOffer");
    });

    peerConnection.onclose = function () {
        console.log('peerConnection closed');
    }
}


function handleCandidate(candidate) {
    peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
    // countIceCandidate = countIceCandidate + 1;
    // buttonsHandler("handleCandidate");
    console.log("handleCandidate");
}


function handleAnswer(answer) {
    peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
    console.log("handleAnswer(answer), countCalls :"+countCalls);
    if(countCalls > 0 && countIceCandidate == 0){
        createOffer();
    }
}

function youHaveCall(data){
    ring.play();
    clearSearchPanel();
    addPhonePic();
    console.log("youHaveCall(), data :",data);
    document.getElementById(data).style.background = "#FFA500";
    countCalls = countCalls+1;
    changeMsg(msg);
    console.log('youHaveCall(), countCalls :'+countCalls);
}

function handleMessage(data){
    changeMsg(data.valueOf());
    console.log('message: '+data.valueOf()+' '+data.val);
}

function handleKey(data){
    key = data.valueOf();
    console.log('key: '+data.valueOf());
    send({
        event:"chats",
        data:login,
        key: key
    });
}

//handleChats starts after successful registration
function handleChats(data){
    if(login != null){

    addUser();
    var list = document.getElementById("usersList");
    while(list.firstChild != null){
        list.firstChild.remove();
    }
    try {
        chats = JSON.parse(data);
    } catch(error){
        console.log(error);
        chats = data;
    }
    chats = new Map(Object.entries(chats));
    console.log("handleChats, users: "+chats);
    for(let chat of chats.keys()){
        if(chat != login){
            let node = document.createElement("li");
            let button = document.createElement("a");
            let textNode = document.createTextNode(chat);
            button.id = chat;

            button.appendChild(textNode);
            node.appendChild(button);
            button.onclick = connectRemoteUser;
            document.getElementById("usersList").appendChild(node);
        }
    }
    changeMsg("You have logged in.");
    }
}

//handle find: users that were searched
function handleFind(data){
    let ul = document.getElementById("searchMenu");
    while(ul.firstChild != null){
        ul.firstChild.remove();
    }

    try {
        foundUsers = JSON.parse(data);
        console.log("handleFind(), foundUsers: " + foundUsers);


        var li, a, i, j;
        let input = document.getElementById("search");
        let filter = input.value.toUpperCase();
        // ul = document.getElementById("searchMenu");

        for (j in foundUsers){
            let node = document.createElement("li");
            let button = document.createElement("a");
            let textNode = document.createTextNode(foundUsers[j]);
            button.id =  foundUsers[j];
            button.onclick = function(){
                let ul =document.getElementById("searchMenu");

                for (let k = 0; k < foundUsers.length; k++){
                    document.getElementById(foundUsers[k]).style.background = "#f6f6f6";
                }
                buttonsHandler("find");
                remoteUser = this.id;
                document.getElementById(this.id).style.background = "green";
                //line where remote user name is written
                let elementRemoteUser = document.getElementById("remoteUser");
                while(elementRemoteUser.firstChild != null){
                    elementRemoteUser.firstChild.remove();
                }
                let textNode = document.createTextNode(remoteUser);
                elementRemoteUser.appendChild(textNode);
            };
            console.log("handleFind(), found user: " + foundUsers[j]);
            // button.className = "btn btn-outline-primary";
            button.appendChild(textNode);
            node.appendChild(button);
            ul.appendChild(node);
        }

        li = ul.getElementsByTagName("li");
        for (i = 0; i < li.length; i++) {
            a = li[i].getElementsByTagName("a")[0];
            if (a.innerHTML.toUpperCase().indexOf(filter) > -1) {
                li[i].style.display = "";
            } else {
                li[i].style.display = "none";
            }
        }
    } catch(error){
        console.log(error);
    }
}


//manage buttons

function buttonsHandler(event){ //command
var toRegBtn;
var toLogBtn;
var logout;
var btnStop;
var logDiv;
var addUser;
var removeUser;
var msgBtn;

    console.log("eventHandler: ", event);
    addUser = document.getElementById("addRemoteUser");
    removeUser = document.getElementById("removeRemoteUser");
    msgBtn = document.getElementById("msgBtn");

    switch (event) {
        case "onloadPage":
            msgBtn.disabled = true;
            break;
        case "stopCall":
            removeUser.style.display = "none";
            break;
        case "hangup":
            // toRegBtn.style.display = "none";
            // toLogBtn.style.display = "none";
            // logout.style.display = "block";
            // btnStop.style.display = "none";
            break;
        case "handleCandidate":
                // toRegBtn.style.display = "none";
                // toLogBtn.style.display = "none";
                // logout.style.display = "block";
                // btnStop.style.display = "block";
            break;
        case "logout":
                // toRegBtn.style.display = "block";
                // toLogBtn.style.display = "block";
                // logout.style.display = "none";
                // btnStop.style.display = "none";
            break;
        case "call":
                // btnStop.style.display = "block";
            break;
        case "find":
            addUser.style.display = "block";
            // removeUser.style.display = "block";
            break;
        case "addChatUserBtn":
            addUser.style.display = "none";
            removeUser.style.display = "none";
            msgBtn.disabled = true;
            break;
        case "removeBtnActiveUser":
            addUser.style.display = "none";
            removeUser.style.display = "block";
            msgBtn.disabled = false;
            break;
        default:
            addUser.style.display = "none";
            removeUser.style.display = "none";
            break;
    }
}

function addPhonePic(){
    let phonePic = document.getElementById("phonePicture");

   removePhonePic();

    let i = document.createElement("i");
    i.className = "fa fa-phone";
    i.onclick = function (){
        call();
    };
    phonePic.appendChild(i);
}

function removePhonePic(){
    let phonePic = document.getElementById("phonePicture");

    while(phonePic.firstChild != null){
        phonePic.firstChild.remove();
    }
}

function addUser(){
    var userDiv = document.getElementById("user");
    userDiv.innerText = login +" [me]";
}

// call and hung up commands ----------------------------------------------------------------
//choose remote user and make call
function connectRemoteUser(event) {
    clearSearchPanel();
    addPhonePic();
    remoteUser = this.id;
    // call();
    lightOffUsers();
    document.getElementById(this.id).style.background = "green";
    var elementRemoteUser = document.getElementById("remoteUser");
    clearRemoteUser();
    var textNode = document.createTextNode(remoteUser);
    elementRemoteUser.appendChild(textNode);
    buttonsHandler("removeBtnActiveUser");
    console.log("connectRemoteUser, this id: "+remoteUser);
}

function clearRemoteUser(){
    var elementRemoteUser = document.getElementById("remoteUser");
    while(elementRemoteUser.firstChild != null){
        elementRemoteUser.firstChild.remove();
    }
}

function call() {
    ring.pause();
    send({
        event:"call",
        data:remoteUser
    });
        createOffer();
    navigator.mediaDevices.getUserMedia({
        video: {
            width: 720,
            height: 480
        }
    })
        .then(function (stream) {
            // document.getElementById("localVideo").srcObject = stream;
            localStream = stream;
            peerConnection.addTrack(stream.getTracks()[0], stream);
        });
    // buttonsHandler("call");
    on();
    console.log("call(): media track was added and createOffer was made, remoteUser:",remoteUser);
}

function hangUp(data) {
    changeMsg(msgPoints);
    document.getElementById("remoteVideo").srcObject = null;
    peerConnection.onicecandidate = null;
    peerConnection.ontrack = null;
    // var stream = document.getElementById("localVideo").srcObject;
    if(localStream != null){
        localStream.stop = function (){
            this.getTracks().forEach(function(track) { track.stop(); });
        };
        localStream.stop();
    }else{
        return;
    //     // let tracks = peerConnection.getTracks();
    //     // for (i in tracks){
    //     //     tracks[i].stop();
    //     // }
    }
    countIceCandidate = 0;
    countCalls = 0;
    peerConnection.close();
    initialize();


    var videos = document.getElementById("videos");
    // document.getElementById("localVideo").remove();
    document.getElementById("remoteVideo").remove();
    // videos.innerHTML += "<video id=\"localVideo\" autoplay></video><video id=\"remoteVideo\" autoplay></video>";
    videos.innerHTML += "<video id=\"remoteVideo\" autoplay></video>";

    lightOffUsers(data);
    console.log("hangUp()");
}


function stopCall() {
    ring.pause();
    clearRemoteUser();
    removePhonePic();
    buttonsHandler("stopCall");
    hangUp();
    send({
        event:"hangup",
        data:login
    });
    lightOffUsers();
    remoteUser = null;
    // buttonsHandler("stopCall");
}

function lightOffUsers(data){

        // addUser();
        var list = document.getElementById("usersList");
        while(list.firstChild != null){
            list.firstChild.remove();
        }
        // chats = new Map(Object.entries(chats));
        console.log("lightOffUsers, chats: "+chats);
        if (chats != null){
            for(let chat of chats.keys()){
                if(chat != login){
                    var node = document.createElement("li");
                    var button = document.createElement("a");
                    var textNode = document.createTextNode(chat);
                    button.id = chat;

                    button.appendChild(textNode);
                    node.appendChild(button);
                    button.onclick = connectRemoteUser;
                    document.getElementById("usersList").appendChild(node);
                }
            }
        }
    console.log('lightOffUsers, data ', data);
}


// register and login-logout commands ------------------------------------------------
var loginReg;
var passwordReg;
var address;
var email;
var login;
var password;

function sendRegistrationData(){
    loginReg = document.getElementById("loginReg").value;
    passwordReg = document.getElementById("passwordReg").value;
    address = document.getElementById("address").value;
    email = document.getElementById("email").value;
    send({
        event:"register",
        data:{
            login:loginReg,
            password:passwordReg,
            address:address,
            email:email,
            id:""
        }
    });
}

function sendLoginData(){
    login = document.getElementById("log").value;
    password = document.getElementById("pass").value;
    send({
        event:"login",
        data:{
            login:login,
            password:password,
            address:"",
            email:"",
            id:""
        }
    });
    password = null;
}

function logout() {
    var list = document.getElementById("usersList");
    while(list.firstChild != null){
        list.firstChild.remove();
    }

    document.getElementById("msg").innerText = "You've logged out. The connection was closed.";
    document.getElementById("user").innerText = null;

    console.log("logout");
}

// elements manipulations ----------------------------------------------------------

        //show regDiv
function regDiv() {
    showElement("regDiv");
}
        //show logDiv
function logDiv() {
    showElement("logDiv");
    // onloadPage();
}
        //show any element
function showElement(element) {
    var x = document.getElementById(element);
    if (x.style.display === "block") {
        x.style.display = "none";
    } else {
        x.style.display = "block";
    }
}

// on load page
function onloadPage(){
    setTimeout(function(){
        createConnection();
        buttonsHandler("onloadPage")

    },250);

};


function logoutChat() {
    var el = document.getElementById("logoutForm");
    el.submit();
}


// on and off overlay screen
function on() {
    document.getElementById("overlay").style.display = "block";
}

function off() {
    stopCall();
    document.getElementById("overlay").style.display = "none";
}

//search panel function -----------------------------------------------------

function search() {
    removePhonePic();
    lightOffUsers();
    var input, filter;
    input = document.getElementById("search");
    filter = input.value.toUpperCase();
    if(filter.length > 2){
        send({
            event : "find",
            data : filter
        });
    }
    buttonsHandler("addChatUserBtn");
    console.log("search(): "+ input.value);
}

function clearSearchPanel(){
    let list = document.getElementById("searchMenu");
    while(list.firstChild != null){
        list.firstChild.remove();
    }
    let search = document.getElementById("search");
    search.value = "";
    buttonsHandler("add");
    console.log("clearSearchPanel()");
}

function sendAddRemoteUser() {
        send({
            event : "add",
            data : remoteUser
        });
        buttonsHandler("add");
    console.log("sendAddRemoteUser(): add "+ remoteUser);
}

function sendRemoveRemoteUser(){
        send({
            event : "remove",
            data : remoteUser
        });
        buttonsHandler();
            let list = document.getElementById("remoteUser");
            while(list.firstChild != null){
                list.firstChild.remove();
            }
        remoteUser = null;
        console.log("sendRemoveRemoteUser(): remove "+ remoteUser);
}

