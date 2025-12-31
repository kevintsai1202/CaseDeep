'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');
const errorMessageSpan = document.querySelector('#errorMessage');

const videoModal = document.getElementById('video-modal');
const closeButton = document.querySelector('.close-button');

const muteButton = document.getElementById('mute-button');
const stopVideoButton = document.getElementById('stop-video-button');
const endCallButton = document.getElementById('end-call-button');
const localVideo = document.getElementById('localVideo');
const remoteVideo = document.getElementById('remoteVideo');

let stompClient = null;
let uId = null;				
let username = null;		
let targetUid = null;		
let targetUsername = null;	
let message = null;

let peerConnection;
let localStream;
let remoteStream;
let remoteCandidate;
let remoteSDP;
let candidates = [];

const servers = {
    iceServers: [
        {
            urls: 'stun:stun.l.google.com:19302'	//RTC ICE Server data
        }
    ]
};

const constraints = {								//RTC constraints
    video: true,
    audio: true
};

//################################
//################################
//################################
//UI function
function appendUserElement(user, usersList) {
	console.log("list user",user);
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = user.uid;

    const userImage = document.createElement('img');
    userImage.src = './img/user_icon.png';
    userImage.alt = user.username;
    
    const onlineImage = document.createElement('img');
    onlineImage.src = './img/online.png';
    onlineImage.alt = 'online';
    
    const offlineImage = document.createElement('img');
    offlineImage.src = './img/offline.png';
    offlineImage.alt = 'offline';

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.username;

    const receivedMsgs = document.createElement('span');
    receivedMsgs.textContent = '0';
    receivedMsgs.classList.add('nbr-msg', 'hidden');
    
    const callImage = document.createElement('img');
    callImage.src = './img/call.png';
    callImage.alt = 'call';
    callImage.targetid = user.uid;
    callImage.addEventListener('click', callClick);
    
    const hangupImage = document.createElement('img');
    hangupImage.src = './img/hangup.png';
    hangupImage.alt = 'hangup';
    hangupImage.addEventListener('click', hangupClick);
    
    const callSpan = document.createElement('span');
	callSpan.appendChild(callImage);
	callSpan.appendChild(hangupImage);

    //listItem.appendChild(userImage);
    (user.status==='ONLINE')?listItem.appendChild(onlineImage):listItem.appendChild(offlineImage)

    listItem.appendChild(usernameSpan);
    listItem.appendChild(receivedMsgs);

    listItem.appendChild(callSpan);
    listItem.addEventListener('click', userItemClick);

    usersList.appendChild(listItem);
}

async function findAndDisplayUsers() {
    const usersResponse = await fetch('/chat/chatuser/alluser');		//get all user list
    let users = await usersResponse.json();
    console.log('all users',users);
    console.log('uId',uId)
    let filterUsers = users.filter(user => String(user.uid) !== uId);
    console.log('filterUsers',filterUsers);
    const usersList = document.getElementById('users');
    usersList.innerHTML = '';

    filterUsers.forEach(user => {
        appendUserElement(user, usersList);
        if (users.indexOf(user) < users.length - 1) {
            const separator = document.createElement('li');
            separator.classList.add('separator');
            usersList.appendChild(separator);
        }
    });
}

function displayMessage(senderId, content) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if (senderId === uId) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}
//end UI function
//################################
//################################
//################################

//################################
//################################
//################################
//addEventListener
usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
window.addEventListener('click', (event) => {
    if (event.target === videoModal) {
        videoModal.style.display = 'none';
        endCall();
    }
});

window.onbeforeunload = () => onLogout();

closeButton.addEventListener('click', () => {
    videoModal.style.display = 'none';
    endCall();
});

muteButton.addEventListener('click', toggleMute);
stopVideoButton.addEventListener('click', toggleVideo);
endCallButton.addEventListener('click', () => {
    videoModal.style.display = 'none';
    endCall();
});
//end addEventListener
//################################

//################################
//listener callback function
async function connect(event) {
	event.preventDefault();
    uId = document.querySelector('#uid').value.trim();
    if (uId) {
		getChatUser(uId);
		usernamePage.classList.add('hidden');
    	chatPage.classList.remove('hidden');
    	const socket = new SockJS(`/ws`);	// full path ws://casedeep.com:8080/ws
    	stompClient = Stomp.over(socket);
   		stompClient.connect({}, onConnected, onError);
   		
   		peerConnection = new RTCPeerConnection(servers);
    }
}
// all websocket type by casedeep
// connect, disconnect, candidate, chat_message, video_offer, video_answer, video_reject, video_hangup, voice_offer, voice_answer, voice_reject, voice_hangup
function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage = {
			type: "chat_message",
            data: {
	            senderId: uId,
	            receiverId: targetUid,
	            content: messageInput.value.trim()
            }
        };
        stompClient.send("/app/chat/message", {}, JSON.stringify(chatMessage));
        displayMessage(uId, messageInput.value.trim());
        messageInput.value = '';
    }
    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}

function onLogout() {
	endCall();
    stompClient.send("/app/disconnect",
        {},
        JSON.stringify({
			type:"disconnect",
			data:{
				uId,
        	}
        })
    );
    window.location.reload();
}

function toggleMute() {
	console.log("toggleMute")
    const audioTracks = localStream.getAudioTracks();
    if (audioTracks.length > 0) {
        audioTracks[0].enabled = !audioTracks[0].enabled;
        muteButton.textContent = audioTracks[0].enabled ? 'Mute' : 'Unmute';
    }
}

function toggleVideo() {
	console.log("toggleVideo")
    const videoTracks = localStream.getVideoTracks();
    if (videoTracks.length > 0) {
        videoTracks[0].enabled = !videoTracks[0].enabled;
        document.getElementById('stop-video-button').textContent = videoTracks[0].enabled ? 'Stop Video' : 'Start Video';
    }
}

function endCall() {
	console.log("endCall")
    if (peerConnection) {
        peerConnection.close();
        peerConnection = null;
    }

    if (localVideo.srcObject) {
        localVideo.srcObject.getTracks().forEach(track => track.stop());
        localVideo.srcObject = null;
    }
    if (remoteVideo.srcObject) {
        remoteVideo.srcObject.getTracks().forEach(track => track.stop());
        remoteVideo.srcObject = null;
    }
}

function callClick(event) {
	const callImg = event.target;
	console.log("callImg", callImg)
	const targetUid = callImg.targetid;
	console.log("targetUid",targetUid)
    //call(targetUid);
    startVideoCall();
}

function hangupClick(event) {
    //TODO process hangup call
}

function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    targetUid = clickedUser.getAttribute('id');
    fetchAndDisplayUserChat().then();

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
    nbrMsg.textContent = '0';

}

function onConnected() {
    stompClient.subscribe(`/queue/messages/${uId}`, onSocketReceived);	//subscribe private channel    
    stompClient.subscribe(`/topic/public`, onSocketReceived);			//subscribe public channel
    
    stompClient.send("/app/connect",
        {},
        JSON.stringify({
			type:"connect",
			data:{
				uId,
        	}
        })
    );
   
    document.querySelector('#user-username').textContent = username;
    findAndDisplayUsers().then();
}
//end listener  callback function
//################################

//pricess websocket signaling message
async function onSocketReceived(payload) {
    await findAndDisplayUsers();
    console.log('Socket received', payload);
    
    const socketData = JSON.parse(payload.body);
    
    const type = socketData.type;
    console.log('Socket type', type);
    
    switch(type){
		case "chat_message":{
			const message = socketData.data;
		    if (targetUid && (targetUid === message.senderId)) {
		        displayMessage(message.senderId, message.content);
		        chatArea.scrollTop = chatArea.scrollHeight;
		    }
		
		    if (targetUid) {
		        document.getElementById(`${targetUid}`).classList.add('active');
		    } else {
		        messageForm.classList.add('hidden');
		    }
		
		    const notifiedUser = document.getElementById(`${message.senderId}`);
		    if (notifiedUser && !notifiedUser.classList.contains('active')) {
		        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
		        nbrMsg.classList.remove('hidden');
		        nbrMsg.textContent = '';
		    }
	    }
		break;
		case "candidate":{	//Sent by the person who initiated the RTC call.
			const message = socketData.data;
			const candidate = new RTCIceCandidate(message.candidate);
			candidates.push(candidate);
			if (peerConnection.remoteDescription){	
				console.log("candidate",candidate)
				peerConnection.addIceCandidate(new RTCIceCandidate(candidate))
				.then(() => {
				  console.log("ICE candidate added successfully.");
				}).catch(error => {
				  console.error("Error adding ICE candidate:", error);
				});
			}
		}
		break;
		case "video_offer":{
				const message = socketData.data;
				targetUid = message.senderId
				remoteSDP = message.sdp;
				console.log("video_offer",remoteSDP);
				offerRecived(remoteSDP)
		}
		break;
		case "video_answer":{
				const message = socketData.data;
				targetUid = message.senderId
				remoteSDP = message.sdp;
				console.log("video_answer",remoteSDP);
				answerRecived(remoteSDP)
		}
	}
}

//init RTC and event process
async function startVideoCall() {
    videoModal.style.display = 'flex';
    
    localStream = await navigator.mediaDevices.getUserMedia(constraints);
    window.stream = localStream;
    localVideo.srcObject = localStream;
    
	peerConnection.addStream(localStream);
    
    
      // When any ICE candidates are available,
  	  // send the candidate to the other party via WebSocket."
    peerConnection.onicecandidate = ({ candidate }) => {
        if (candidate?.candidate) {
            console.log('candidate', candidate);
            stompClient.send("/app/candidate",
		        {},
		        JSON.stringify({
					type:"candidate",
					data:{
						senderId: uId,
				    	receiverId: targetUid,
				    	candidate: candidate
		        	}
		        })
		    );
        }
    };
    
    // Indicates that the environment is OK, and an Offer can be created and sent.
    peerConnection.onnegotiationneeded = () => {
		console.log("createOffer")
	    peerConnection.createOffer(offerCreated, onError);
	}
    
    // Received remote stream, adding it to the remote video component.
    peerConnection.ontrack = event => {
		console.log("get Remote Video!")
        remoteVideo.srcObject = event.streams[0];
    };
}

async function getChatUser(uId) {
    const chatUserResponse = await fetch(`/chat/chatuser/${uId}`);
    let chatUser = await chatUserResponse.json();
    username = chatUser.username
}

async function fetchAndDisplayUserChat() {
    const userChatResponse = await fetch(`/chat/messages/${uId}/${targetUid}`);
    const userChat = await userChatResponse.json();
    chatArea.innerHTML = '';
    userChat&&userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content);
    });
    chatArea.scrollTop = chatArea.scrollHeight;
}

function onError(error) {
	console.log("error",error)
    connectingElement.textContent = error;
    connectingElement.style.color = 'red';
}


function offerCreated(desc) {
  peerConnection.setLocalDescription(desc, () => {
    stompClient.send("/app/video/offer", {},
	        JSON.stringify({
				type:"video_offer",
				data:{
					senderId: uId,
			    	receiverId: targetUid,
			    	sdp: desc
	        	}
	        })
	);
  }, onError);
}

function answerCreated(desc) {
  peerConnection.setLocalDescription(desc, () => {
    stompClient.send("/app/video/answer", {},
	        JSON.stringify({
				type:"video_answer",
				data:{
					senderId: uId,
			    	receiverId: targetUid,
			    	sdp: desc
	        	}
	        })
	);
	startVideoCall();	//Start the video after answer the call
  }, onError);
}

function offerRecived(desc) {	//Reciver's offer
  peerConnection.setRemoteDescription(
	  new RTCSessionDescription(desc), ()=>{
			// ICE candidates that the receiver couldn't add with peerConnection.addIceCandidate earlier are stored in candidates
			// Once the offer is received, all of them can be added."
		  console.log("candidates size", candidates.length)
		  candidates.forEach(candidate => {
	            peerConnection.addIceCandidate(candidate)
	            .catch(error => {
	                console.error('Error adding ICE candidate:', error);
	            });
	        });
		  
	      console.log("get offer!");
    }, onError);
  peerConnection.createAnswer(answerCreated, onError)
}

function answerRecived(desc) {	// answer reciver's offer
  peerConnection.setRemoteDescription(
	  new RTCSessionDescription(desc), ()=>{
	      console.log("get answer!");
    }, onError);
}





