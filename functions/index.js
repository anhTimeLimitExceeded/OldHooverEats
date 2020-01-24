const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original

exports.requestingSwipes = functions.firestore.document('requestSwipes/{requestID}').onCreate(event => {
	var citiesRef = admin.firestore().collection('provideSwipes');
	var allCities = citiesRef.get().then(snapshot => {
		snapshot.forEach(doc => {
			const userID = doc.id;
			const tokenID = doc.data().tokenID;
			var payload = {
				notification: {
					title: "New Request",
					body: "Someone is requesting",
					icon: "default"
				}
			};

			admin.messaging().sendToDevice(tokenID, payload).then(result => {
				console.log("Notification sent to " + tokenID);
				return null;		
			})
			.catch((error) => {
				console.log('Error sending message:', error);
			});
			//console.log(doc.id, '=>', doc.data());
		})			
			return null;
	})
	.catch(err => {
		console.log('Error getting documents', err);
		return null;
	});
}); 