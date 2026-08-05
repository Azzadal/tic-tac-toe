import { createReducer } from '@reduxjs/toolkit';
import cloneDeep from 'lodash/cloneDeep';
import { setBlock, setUserAction, setField, setWin, setState } from './actions';
import { GameField, Value } from '../model/field';

const initState = new GameField();

interface IState {
  field: Value[][];
  blocking: boolean;
}
export const ticTacTorReducer = createReducer<IState>({ field: [], blocking: false }, (builder) => {
  builder
    .addCase(setField, (state, action) => {
      console.log('update game field');
    })
    .addCase(setUserAction, (state, action) => {
      console.log('action', action.payload);
      // const copy = cloneDeep(state);
      // copy.setValue(action.payload.position, action.payload.value);
      const field = { ...state.field };
      field[action.payload.position.x][action.payload.position.y] = action.payload.value;
      return { ...state, field };
    })
    .addCase(setBlock, (state, action) => {
      console.log('запрос на блокирование интерфейса', action.payload);
      // const copy = cloneDeep(state);
      // copy.block = !!action.payload;
      return { ...state, blocking: !!action.payload };
    })
    .addCase(setState, (state, action) => {
      console.log('state', state);
      console.log('запрос на обновление поля', action.payload);
      // const copy = cloneDeep(state);
      // copy.setField(JSON.parse(JSON.stringify(action.payload.field)));
      return { ...state, field: action.payload.field };
    });
});
