import React from 'react';
import { v4 } from 'uuid';
import { Field } from './components/field/Field';

export const MainPage: React.FC = () => {
  if (!localStorage.getItem('uuid')) localStorage.setItem('uuid', v4());
  return <Field />;
};
