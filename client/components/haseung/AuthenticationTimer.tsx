/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-16
 * 최근 수정일: 2022-11-16
 */

import { useEffect, useState } from 'react';

export const AuthenticationTimer = () => {
  const [minutes, setMinutes] = useState(5);
  const [seconds, setSeconds] = useState(0);

  useEffect(() => {
    const countDown = setTimeout(() => {
      if (seconds > 0) setSeconds(seconds - 1);
      if (seconds === 0) {
        if (minutes === 0) clearTimeout(countDown);
        else {
          setMinutes(minutes - 1);
          setSeconds(59);
        }
      }
    }, 1000);
    return () => clearTimeout(countDown);
  }, [minutes, seconds]);

  return (
    <h2>
      {minutes}:{seconds < 10 ? `0${seconds}` : seconds}
    </h2>
  );
};
