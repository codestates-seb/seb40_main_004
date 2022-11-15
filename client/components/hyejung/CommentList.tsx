import { useState } from 'react';
import { Comment } from './Comment';

export const CommentList = () => {
  const [isOpen, setIsOpen] = useState(false);
  return (
    <>
      <Comment />
      {isOpen ? (
        <>
          {[1, 2].map((el) => (
            <Comment key={el} />
          ))}
          <button onClick={() => setIsOpen(false)}>닫기</button>
        </>
      ) : (
        <button onClick={() => setIsOpen(true)}>펼치기</button>
      )}
    </>
  );
};
