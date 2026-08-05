import { Col, Grid, Row } from 'antd';
import React from 'react';
import { Cell } from './Cell';
import { useSelector } from 'react-redux';
import { RootState } from '../../store/app';
import './style.scss';

interface IFieldProps {
  className?: string;
  socket?: WebSocket;
}
export const Field: React.FC<IFieldProps> = ({ className, socket }) => {
  const gameFieldState = useSelector((root: RootState) => root.ticTacTorReducer);
  console.log('gameFieldState', gameFieldState);
  return (
    <div className="w-100 mt-5">
      {gameFieldState.field?.map((row, i) => {
        return (
          <Row key={i} className="py-3" gutter={50} justify={{ lg: 'center' }}>
            {row.map((cell, j) => {
              return (
                <Col key={j}>
                  <Cell disabled={gameFieldState.blocking} value={cell} position={{ x: i, y: j }} />
                </Col>
              );
            })}
          </Row>
        );
      })}
    </div>
  );
};
