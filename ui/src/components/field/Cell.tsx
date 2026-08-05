import React, { useContext, useEffect, useState } from 'react';
import cn from 'classnames';
import type { IMessage } from '../../model/message';
import { OutComingCommands } from '../../ws/action';
import type { Position, Value } from '../../model/field';
import { SocketContext } from '../../ws/socket';

interface ICellProps {
  value?: Value;
  disabled?: boolean;
  position: Position;
  className?: string;
  onClick?: (value: Value) => void;
}
export const Cell: React.FC<ICellProps> = ({ className, value, disabled, position, onClick }) => {
  const uuid: string | null = sessionStorage.getItem('uuid');
  const socket = useContext(SocketContext);
  // console.log('value', value);

  return (
    <>
      {socket ? (
        <div
          className={cn('field__cell', className, { field__cell_disabled: disabled })}
          onClick={() => {
            console.log('click', disabled, position, value);
            if (!disabled && !value) {
              socket.send(
                JSON.stringify({
                  type: OutComingCommands.ACTION,
                  data: {
                    playerId: uuid,
                    position,
                    value: 'X',
                  },
                } as IMessage)
              );
            }
          }}
          onContextMenu={(event) => {
            event.preventDefault();
            if (!disabled && !value) {
              socket.send(
                JSON.stringify({
                  type: OutComingCommands.ACTION,
                  data: {
                    playerId: uuid,
                    position,
                    value: 'O',
                  },
                } as IMessage)
              );
            }
          }}
        >
          {value ?? '-'}
        </div>
      ) : (
        <span>Ожидание подключения...</span>
      )}
    </>
  );
};
