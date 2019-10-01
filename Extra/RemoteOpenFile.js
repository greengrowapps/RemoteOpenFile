
const msgType_HELLO_FROM_CLIENT = "1";
const msgType_HELLO_FROM_SERVER = "2";
const msgType_REMOTE_OPEN_FILE = "3";
const msgType_ECHO = "4";


function Rof(evListener){
    this.evListener={
        connectOnPort:function (port,projects) {

        },
        closedOnPort:function (port,aliveConnections) {

        }
    };
    this.evListener=evListener;
    this.websocketMap={};
    this.wsUri = "ws://localhost"
}


Rof.prototype.conect=function() {
    for(var i=8880 ; i<=8889;i++){
        var current = null;
        try {
            current = new WebSocket(this.wsUri + ":" + i );
        }catch (e) {
            console.log(e);
            continue;
        }
        current.onopen = function (evt) {
            var port=getPort( evt.target.url);
            console.log("OnOpen("+port+"):"+evt.data)
            var msg={msgT:msgType_HELLO_FROM_CLIENT,
                     origin:window.location.origin,
                     path:window.location.pathname,
                     search:window.location.search};
            evt.target.send(JSON.stringify(msg));
        }.bind(this);
        current.onclose = function (evt) {
            var port=getPort( evt.target.url);
            console.log("OnClose("+port+"):"+evt.data)
            if(this.websocketMap[port]) {
                delete this.websocketMap[port];
                this.evListener.closedOnPort(port, Object.keys(this.websocketMap).length > 0)
            }
        }.bind(this);
        current.onmessage = function (evt) {
            var port=getPort( evt.target.url);
            console.log("onMessage("+port+"):"+evt.data);
            var msg=JSON.parse(evt.data);
            if(msg.msgT == msgType_HELLO_FROM_SERVER){
                var projectsList=msg.projects;
                this.websocketMap[port]=evt.target;
                this.evListener.connectOnPort(port,projectsList);
            }else{
                console.log("onMessage("+port+") -- Unknown message: ("+msg.msgT+") "+evt.data);
            }
        }.bind(this)
        current.onerror = function (evt) {
            var port=getPort( evt.target.url);
            console.log("OnError("+port+"):"+evt.data)
        }.bind(this);
    }
}
Rof.prototype.close=function(){
    for(var port in this.websocketMap){
        this.websocketMap[port].close();
    }
}

Rof.prototype.openFileAndLine=function(file , line){
    for(var port in this.websocketMap){
        var msg={msgT:msgType_REMOTE_OPEN_FILE,
                filePath:file,
                line:parseInt(line)};
        this.websocketMap[port].send(JSON.stringify(msg));
    }
}

function getPort(url) {
 var startIndex=url.lastIndexOf(":")+1;
  var portString= url.substr(startIndex,url.length-startIndex-1);
    portString=portString.replace("/","");
  return parseInt(portString);
}