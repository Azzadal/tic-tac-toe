import { setBlock, setState, setUserAction } from '../store/actions';
import { AppDispatch } from '../store/app';
import { InComingCommands } from './action';
import { notification } from 'antd';

function webSocketBaseHandler(socket: WebSocket) {
  socket.addEventListener('open', (event) => {
    console.log('WebSocket connection established!', event.type);
  });

  socket.addEventListener('close', (event) => {
    console.log('%cWebSocket connection closed:', 'color:green', event.code, event.reason);
  });

  socket.addEventListener('error', (error) => {
    console.error('WebSocket error:', error);
  });
}

function webSocketMessageHandler(socket: WebSocket, dispatch: AppDispatch, onSid: (sid: string) => void) {
  socket.addEventListener('message', (event) => {
    try {
      console.log('###', event.data);

      const receivedData = JSON.parse(event.data) as { type: string; data: any };
      console.log('Received JSON:', receivedData);
      switch (receivedData.type) {
        case InComingCommands.SID:
          console.log('%cget sid', 'color:red', receivedData.data);
          onSid(receivedData.data);
          break;
        case InComingCommands.FIELD:
          dispatch(setState(receivedData.data));
          break;
        case InComingCommands.LOCK:
          notification.warning({ message: InComingCommands.LOCK });
          dispatch(setBlock(true));
          break;
        case InComingCommands.UNLOCK:
          console.log('UNLOCK');

          dispatch(setBlock(false));
          break;
        case InComingCommands.ANSWER:
          dispatch(
            setUserAction({
              position: JSON.parse(receivedData.data).position,
              value: JSON.parse(receivedData.data).value,
            })
          );
          break;
        case InComingCommands.WIN:
          setTimeout(() => {
            console.log('game over, winner is', receivedData.data);
            dispatch(setBlock(true));
            notification.success({ message: `Победил: ${receivedData.data}` });
            localStorage.setItem('sid', '');
          }, 0);
          // еще какая-то логика для победы
          break;
      }
    } catch (error) {
      console.error('Error parsing JSON:', error);
      console.log('Received data was:', event.data);
      if (error && typeof error === 'object' && 'message' in error)
        if (typeof error.message === 'string') notification.error({ message: error.message });
    }
  });
}

export function ticTacToeSocketHandler(socket: WebSocket, dispatch: AppDispatch, onSid: (sid: string) => void) {
  webSocketBaseHandler(socket);
  webSocketMessageHandler(socket, dispatch, onSid);
}
