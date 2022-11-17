import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBookmark as SolidBookmark } from '@fortawesome/free-solid-svg-icons';
import { faBookmark as VoidBookmark } from '@fortawesome/free-regular-svg-icons';
import { useState } from 'react';

export const BtnBookmark = () => {
  const [isMarked, setIsMarked] = useState(true);
  return (
    <>
      <button onClick={() => setIsMarked((prev) => !prev)}>
        {isMarked ? (
          <FontAwesomeIcon
            icon={SolidBookmark}
            style={{ color: '#A0A0A0' }}
            className="fa-xl"
          />
        ) : (
          <FontAwesomeIcon
            icon={VoidBookmark}
            className="fa-xl"
            style={{ color: '#A0A0A0' }}
          />
        )}
      </button>
    </>
  );
};
