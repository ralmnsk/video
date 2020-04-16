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


function createMsg(msg) {
    var line = document.getElementById("line");
    line.innerHTML = "<h3 id=\"msg\">"+msg+"</h3>";
}

function changeMsg(msg){
    document.getElementById("msg").remove();
    // setTimeout(function (){console.log('delete msg')},3000);
    createMsg(msg);
}

//connecting to our signaling server

function createConnection(){
    conn = new WebSocket('ws://localhost:8081/socket');

    conn.onopen = function() {
        console.log("Connected to the signaling server");
        initialize();
        changeMsg("connected to the signaling server");
    };

    conn.onclose = function () {
        console.log("Connection closed.");
    };

    conn.onmessage = function(msg) {
        console.log("Got message", msg.data);
        let content = JSON.parse(msg.data);
        let data = content.data;
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
                youHaveCall();
                break;
            case "hangUp":
                hangUp();
                break;
            case "message":
                handleMessage(data);
                break;
            case "key":
                handleKey(data);
                break;
            case "users":
                handleUsers(data);
                break;
            default:
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

// handlers------------
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
    console.log("handleCandidate");
}


function handleAnswer(answer) {
    peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
    console.log("handleAnswer(answer), countCalls :"+countCalls);
    if(countCalls > 0 && countIceCandidate == 0){
        createOffer();
    }
}

function youHaveCall(){
    countCalls = countCalls+1;
    changeMsg(msg);
    console.log('youHaveCall, countCalls :'+countCalls);
}

function handleMessage(data){
    changeMsg(data.valueOf());
    console.log('message: '+data.valueOf()+' '+data.val);
}

function handleKey(data){
    key = data.valueOf();
    console.log('key: '+data.valueOf());
    send({
        event:"users",
        data:login,
        key: key
    });
}

function handleUsers(data){
    addUser();
    var list = document.getElementById("usersList");
    while(list.firstChild != null){
        list.firstChild.remove();
    }
    // data = "{" + data.substring(0,data.length)+"}";
    // console.log("handleUsers, data: "+data);
    try {
        users = JSON.parse(data);
    } catch(error){
        console.log(error);
        users = data;
    }
    console.log("handleUsers, users: "+users);
    for(i in users){
        if(users[i] != login){
            var node = document.createElement("li");
            var button = document.createElement("button");
            var textNode = document.createTextNode(users[i]);
            var remoteUser = users[i];
            button.id = remoteUser;
            button.appendChild(textNode);
            node.appendChild(button);
            button.onclick = connectRemoteUser;
            document.getElementById("usersList").appendChild(node);
        }
    }
}

function addUser(){
    var userDiv = document.getElementById("user");
    userDiv.innerText = login +" [me]";
}

// call commands ----------------------------------------------------------------
function connectRemoteUser(event) {
    send({
       event:"connect",
        data: this.id,            //a remote user
        key:key
    });
    console.log("connectRemoteUser, this id: "+this.id);
}

function call() {
        createOffer();
    send({
        event:"call",
        data:"call"
    });
    navigator.mediaDevices.getUserMedia({
        video: {
            width: 480,
            height: 360
        }
    })
        .then(function (stream) {
            document.getElementById("localVideo").srcObject = stream;
            peerConnection.addTrack(stream.getTracks()[0], stream);
        });

    console.log("call(): media track was added and createOffer was made");
}

function hangUp() {
    changeMsg(msgPoints);
    document.getElementById("remoteVideo").srcObject = null;
    peerConnection.onicecandidate = null;
    peerConnection.ontrack = null;
    var stream = document.getElementById("localVideo").srcObject;
    stream.stop = function (){
        this.getTracks().forEach(function(track) { track.stop(); });
    };
    stream.stop();
    countIceCandidate = 0;
    countCalls = 0;
    peerConnection.close();
    initialize();

    var videos = document.getElementById("videos");
    document.getElementById("localVideo").remove();
    document.getElementById("remoteVideo").remove();
    videos.innerHTML += "<video id=\"localVideo\" autoplay></video><video id=\"remoteVideo\" autoplay></video>";
    console.log("hangUp()");
}

function stopCall() {
    hangUp();
    send({
        event:"hangUp",
        data:"hangUp"
    });
}

// register and login commands
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
    login = document.getElementById("login").value;
    password = document.getElementById("password").value;
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

    conn.close();
    console.log("logout");
}

        //show regDiv
function regDiv() {
    showElement("regDiv");
}
        //show logDiv
function logDiv() {
    showElement("logDiv");
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


//--------------------------------------------------------------------------

//--------------------------------------------------------------------------
// function test() {
//     peerConnection.createOffer(function(answer) {
//         send({
//             event: "answer",
//             data: answer,
//         });
//         peerConnection.setLocalDescription(answer);
//     }, function(err) {
//         console.log("error createOffer() :"+err);
//     });
//     console.log("createOffer() in test");
// }



