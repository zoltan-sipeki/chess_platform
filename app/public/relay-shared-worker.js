const ports = [];
let socket = null;

onconnect = e => {
    const port = e.ports[0];
    ports.push(port);

    port.onmessage = e => {
        if (socket != null && socket.readyState == WebSocket.OPEN) {
            socket.send(JSON.stringify(e.data));
        }
    };

    if (socket == null) {
        socket = new WebSocket("ws://localhost:8081/relay/ws");
        socket.onopen = () => {
            for (const port of ports) {
                port.postMessage({ type: "OPEN" });
            }
        };

        socket.onmessage = e => {
            for (const port of ports) {
                port.postMessage(JSON.parse(e.data));
            }
        };

        socket.onerror = e => {
            console.log(e);
        };

        socket.onclose = e => {
            console.log(e);
        };
    }
}