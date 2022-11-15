import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeart as SolidHeart } from '@fortawesome/free-solid-svg-icons';
import { faHeart as VoidHeart } from '@fortawesome/free-regular-svg-icons';
import { useState } from 'react';

export const BtnLike = () => {
  const [isLike, setIsLike] = useState(true);
  return (
    <>
      <button onClick={() => setIsLike((prev) => !prev)}>
        {isLike ? (
          <FontAwesomeIcon
            icon={SolidHeart}
            style={{ color: '#FF9F10' }}
            className="fa-xl"
          />
        ) : (
          <FontAwesomeIcon icon={VoidHeart} className="fa-xl" />
        )}
      </button>
    </>
  );
};
