type MsgProps = {
  msg: string | undefined;
};

export const ValidationMsg = ({ msg }: MsgProps) => {
  return <p className="text-sm text-red-500">{msg}</p>;
};
