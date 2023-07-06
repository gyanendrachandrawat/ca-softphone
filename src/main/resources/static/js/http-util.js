function sendRequest(url, method, data) {
  return fetch(url, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
        // Add any other headers as needed
      },
      body: JSON.stringify(data)
    })
    .then(function (response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('Network response was not ok.');
      }
    })
    .then(function (responseData) {
      return responseData;
    })
    .catch(function (error) {
      console.log(error);
    });
}
$(function () {
  $('#send-msg').click(function (event) {
    event.preventDefault(); // Prevents the default form submission

    // Get form data
    var to = document.getElementById('to').value;
    var message = document.getElementById('message').value;
    var from = document.getElementById('myInput').value;
    var body = {
      "to": to,
      "body": message,
      "from": from
    }
    console.log(body);
    sendRequest("/sms/sendsms", "POST", body)
      .then(function (data) {
        console.log(data);
        $('#button-refresh').trigger('click');
      });

  });
});