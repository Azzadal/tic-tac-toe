import React, { useEffect, useState } from 'react';
import { Button } from 'antd';
import type { IGame } from '../../model/game';

interface ILeftBarProps {
  className?: string;
}
export const LeftBar: React.FC<ILeftBarProps> = ({ className }) => {
  const [games, setGames] = useState<IGame[]>([]);
  const handleNewGame = () => {
    localStorage.setItem('sid', '');
    window.location.reload();
  };

  useEffect(() => {
    const eventSource = new EventSource('/api/lobby');

    eventSource.onmessage = (event) => {
      const updatedGame = JSON.parse(event.data);

      setGames((prev) => {
        if (updatedGame.status === 'FINISHED') {
          return prev.filter((g) => g.id !== updatedGame.id);
        }
        const exists = prev.some((g) => g.id === updatedGame.id);
        if (exists) {
          return prev.map((g) => (g.id === updatedGame.id ? updatedGame : g));
        }
        return [...prev, updatedGame];
      });
    };

    eventSource.onerror = (err) => {
      console.error('Lobby stream failed:', err);
      eventSource.close();
    };

    return () => eventSource.close();
  }, []);

  return (
    <>
      <Button onClick={handleNewGame}>Новая игра</Button>
      <div>left bar</div>
    </>
  );
};
