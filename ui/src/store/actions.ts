import { createAction } from '@reduxjs/toolkit';
import { GameField, Position, Value } from '../model/field';

export const setUserAction = createAction<{ position: Position; value: Value }>('@@field/setUserAction');
export const setBlock = createAction<boolean>('@@field/setBlock');
export const setState = createAction<{ field: Value[][] }>('@@field/setState');
export const setField = createAction('@@field/setField');
export const setWin = createAction<{ playerId: string }>('@@field/setWin');
