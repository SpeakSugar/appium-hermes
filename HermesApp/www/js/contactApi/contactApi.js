function contactApi() {
    webserver.onRequest(
        function (request) {
            if (request.path === '/testApi') {
                response(request, {returnCode: 200, returnMsg: "connect success"})
            }

            if (request.path === '/addContact') {
                window.ContactsX.save(
                    JSON.parse(request.body),
                    function (success) {
                        response(request, {returnCode: 200, returnMsg: "add contact success"})
                    }, function (error) {
                        response(request, {returnCode: 500, returnMsg: JSON.stringify(request.body)})
                    })
            }

            if (request.path === '/findContact') {
                window.ContactsX.find(function (contacts) {
                    response(request, {returnCode: 200, returnMsg: "find contact success", content: contacts})
                }, function (error) {
                    response(request, {returnCode: 500, returnMsg: JSON.stringify(error)})
                }, {
                    fields: {
                        firstName: true,
                        familyName: true,
                        phoneNumbers: true,
                        emails: true
                    }
                })
            }

            if (request.path === '/deleteContact') {
                window.ContactsX.delete(
                    JSON.parse(request.body),
                    function (success) {
                        response(request, {returnCode: 200, returnMsg: "delete contact success"})
                    }, function (error) {
                        response(request, {returnCode: 500, returnMsg: JSON.stringify(request.body)})
                    })
            }
        }
    );
}