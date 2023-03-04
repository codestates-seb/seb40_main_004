import React from 'react';

type OnCancelClickProps = {
  onCancelClick: () => void;
};

const FormButtonGroup = ({ onCancelClick }: OnCancelClickProps) => {
  return (
    <article className="flex justify-center py-24">
      <input
        onClick={onCancelClick}
        className="justify-center mx-2 bg-main-gray bg-opacity-80 px-4 py-2 rounded-full cursor-pointer hover:bg-main-gray hover:bg-opacity-100"
        type="submit"
        value="취소"
      />
      <input
        className="justify-center mx-2 bg-main-yellow bg-opacity-80 px-4 py-2 rounded-full cursor-pointer hover:bg-main-yellow hover:bg-opacity-100 "
        type="submit"
        value="등록"
      />
    </article>
  );
};

export default FormButtonGroup;
