import React from 'react';
import { MainPage } from './MainPage';
import { Layout } from './Layout/Layout';
import { SocketProvider } from './ws/socket';
import 'bootstrap/dist/css/bootstrap.min.css';
import { LeftBar } from './components/left-bar/LeftBar';

export const App: React.FC = () => {
  return (
    <Layout>
      <LeftBar />
      <SocketProvider>
        <MainPage />
      </SocketProvider>
    </Layout>
  );
};
