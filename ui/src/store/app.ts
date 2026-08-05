import { useDispatch } from 'react-redux';
import { combineReducers, configureStore } from '@reduxjs/toolkit';
import { ticTacTorReducer } from './reducer';

const rootReducer = combineReducers({
  ticTacTorReducer,
});

const store = configureStore({
  reducer: rootReducer,
});

type DispatchFunc = () => AppDispatch;
export const useAppDispatch: DispatchFunc = useDispatch;
export type RootState = ReturnType<typeof rootReducer>;
export type AppDispatch = typeof store.dispatch;

export default store;
