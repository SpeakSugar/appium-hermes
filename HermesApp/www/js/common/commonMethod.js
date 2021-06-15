function response(request, body) {
    webserver.sendResponse(
        request.requestId,
        {
            status: 200,
            body: JSON.stringify(body),
            headers: {
                'Content-Type': 'application/json'
            }
        }
    );
}