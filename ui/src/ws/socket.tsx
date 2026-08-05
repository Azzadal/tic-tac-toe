import React, { createContext, useEffect, useState } from 'react';
import { useLocalStorage } from 'react-use';
import { useAppDispatch } from '../store/app';
import { ticTacToeSocketHandler } from './handler';

export const SocketContext = createContext<WebSocket | undefined>(undefined);

interface ISocketProps {
  children: React.ReactNode;
}
export const SocketProvider: React.FC<ISocketProps> = ({ children }) => {
  const [sid, setSid] = useLocalStorage<string>('sid', '');
  const [socket, setSocket] = useState<WebSocket>();

  const dispatch = useAppDispatch();

  useEffect(() => {
    console.log('sid is changed', sid);

    setSocket(
      new WebSocket(`ws://localhost:1111/tictactoe?uuid=${localStorage.getItem('uuid')}${sid ? `&sid=${sid}` : ''}`)
    );
    return () => {
      console.log('%cexit', 'color:gray', sid);
      socket?.close();
    };
  }, [sid]);

  useEffect(() => {
    socket &&
      ticTacToeSocketHandler(socket, dispatch, (_sid) => {
        console.log('sid is got', sid, _sid);
        sid !== _sid && setSid(_sid);
      });
  }, [socket]);

  return (
    <>
      <span>{sid}</span>
      <SocketContext.Provider value={socket}>{children}</SocketContext.Provider>
    </>
  );
};
