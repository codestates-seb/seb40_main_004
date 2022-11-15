import { useState } from 'react';
import { Comment } from './Comment';
import { TextArea } from './TextArea';

export const CommentList = () => {
  const [isOpen, setIsOpen] = useState(false);
  return (
    <div className="space-y-8 pt-4">
      <Comment />
      {isOpen ? (
        <>
          {[1, 2].map((el) => (
            <Comment key={el} />
          ))}
          <TextArea />
          <button onClick={() => setIsOpen(false)}>닫기</button>
        </>
      ) : (
        <button onClick={() => setIsOpen(true)}>펼치기</button>
      )}
    </div>
  );
};
