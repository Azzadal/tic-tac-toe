import React from 'react';
import { Link, Outlet } from 'react-router-dom';

interface ILayoutProps {
  children?: React.ReactNode;
}
export const Layout: React.FC<ILayoutProps> = ({ children }) => {
  return (
    <>
      <div className="wrapper">
        <header>
          <div className="header_nav">
            !!!
            {/* <Link to="car">Автомобили</Link>
            <Link to="trade-in">Trade-In</Link>
            <Link to="test-drive">Test-drive</Link>
            <Link to="service">Обслуживание</Link>
            <Link to="car-shering">Car-sharing</Link> */}
          </div>
          {/* <UserInfoTopBar /> */}
        </header>
        <main className="container">{children}</main>

        <footer>
          <a href="https://github.com/Azzadal/tic-tac-toe">Крестики-нолики 2026</a>
        </footer>
      </div>
    </>
  );
};
