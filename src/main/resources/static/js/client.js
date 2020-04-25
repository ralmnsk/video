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
var remoteUser;

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
    buttonsHandler("handleCandidate");
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
        event:"users",
        data:login,
        key: key
    });
}

//handleUsers starts after successful registration
function handleUsers(data){
    if(login != null){

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
            button.className = "btn btn-outline-primary";
            button.appendChild(textNode);
            node.appendChild(button);
            button.onclick = connectRemoteUser;
            document.getElementById("usersList").appendChild(node);
        }
    }
    buttonsHandler("handleUsers");
    changeMsg("You have logged in.");
    }
}

//manage buttons

function buttonsHandler(event){ //command
var toRegBtn;
var toLogBtn;
var logout;
var btnStop;
var logDiv;

    console.log("eventHandler: ", event);
    //when user connected disable an registration, login and enable logout and stop call buttons
    // toRegBtn = document.getElementById("toRegBtn"); //.style.display = view2;
    // toLogBtn = document.getElementById("toLogBtn"); //.style.display = view2;
    // logout = document.getElementById("logout"); //.style.display = view1;
    btnStop = document.getElementById("btnStop");
    btnStop.className = "btn btn-primary";
    // logDiv = document.getElementById("logDiv");

    switch (event) {
        case "onloadPage":
                // toRegBtn.style.display = "block";
                // toLogBtn.style.display = "block";
                // logout.style.display = "none";
                btnStop.style.display = "none";
            break;
        case "handleUsers":
                // toRegBtn.style.display = "none";
                // toLogBtn.style.display = "none";
                // logout.style.display = "block";
                // logDiv.style.display = "none";
            break;
        case "stopCall":
                // toRegBtn.style.display = "none";
                // toLogBtn.style.display = "none";
                // logout.style.display = "block";
                btnStop.style.display = "none";
            break;
        case "hangup":
            // toRegBtn.style.display = "none";
            // toLogBtn.style.display = "none";
            // logout.style.display = "block";
            btnStop.style.display = "none";
            break;
        case "handleCandidate":
                // toRegBtn.style.display = "none";
                // toLogBtn.style.display = "none";
                // logout.style.display = "block";
                btnStop.style.display = "block";
            break;
        case "logout":
                // toRegBtn.style.display = "block";
                // toLogBtn.style.display = "block";
                // logout.style.display = "none";
                btnStop.style.display = "none";
            break;
        case "call":
                btnStop.style.display = "block";
            break;
        default:
            break;
    }
}

function addUser(){
    var userDiv = document.getElementById("user");
    userDiv.innerText = login +" [me]";
}

// call and hung up commands ----------------------------------------------------------------
//choose remote user and make call
function connectRemoteUser(event) {
    remoteUser = this.id;
    call();
    document.getElementById(this.id).style.background = "#FFA500";
    // document.getElementById("btnStop").display = "block";
    // document.getElementById("btnStop").disabled = false;
    console.log("connectRemoteUser, this id: "+this.id);
}


function call() {

    send({
        event:"call",
        data:remoteUser
    });
        createOffer();
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
    buttonsHandler("call");
    console.log("call(): media track was added and createOffer was made, remoteUser:",remoteUser);
}

function hangUp(data) {
    changeMsg(msgPoints);
    document.getElementById("remoteVideo").srcObject = null;
    peerConnection.onicecandidate = null;
    peerConnection.ontrack = null;
    var stream = document.getElementById("localVideo").srcObject;
    if(stream != null){
        stream.stop = function (){
            this.getTracks().forEach(function(track) { track.stop(); });
        };
        stream.stop();
    }else{
        return;
        // let tracks = peerConnection.getTracks();
        // for (i in tracks){
        //     tracks[i].stop();
        // }
    }
    countIceCandidate = 0;
    countCalls = 0;
    peerConnection.close();
    initialize();

    var videos = document.getElementById("videos");
    document.getElementById("localVideo").remove();
    document.getElementById("remoteVideo").remove();
    videos.innerHTML += "<video id=\"localVideo\" autoplay></video><video id=\"remoteVideo\" autoplay></video>";
    document.getElementById("btnStop").display = "none";

    lightOffUsers(data);
    buttonsHandler("hangup");
    console.log("hangUp()");
}


function stopCall() {
    hangUp();
    send({
        event:"hangup",
        data:login
    });
    lightOffUsers();
    remoteUser = null;
    buttonsHandler("stopCall");
}

function lightOffUsers(data){

        // addUser();
        var list = document.getElementById("usersList");
        while(list.firstChild != null){
            list.firstChild.remove();
        }

        console.log("lightOffUsers, users: "+users);
        for(i in users){
            if(users[i] != login){
                var node = document.createElement("li");
                var button = document.createElement("button");
                var textNode = document.createTextNode(users[i]);
                var remoteUser = users[i];
                button.id = remoteUser;
                button.appendChild(textNode);
                button.className = "btn btn-outline-primary";
                node.appendChild(button);
                button.onclick = connectRemoteUser;
                document.getElementById("usersList").appendChild(node);
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

    // conn.close();

    buttonsHandler("logout");
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
        buttonsHandler("onloadPage");

    },500);

};



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



