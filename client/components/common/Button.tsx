import { MouseEventHandler } from 'react';

type ButtonText = {
  children: string;
  funcProp?: MouseEventHandler<HTMLButtonElement>;
};

export const Button = ({ children, funcProp }: ButtonText) => {
  return (
    <button
      className="bg-main-yellow text-[16px] px-5 py-[6px] rounded-full"
      type="submit"
      onClick={funcProp}
    >
      {children}
    </button>
  );
};
