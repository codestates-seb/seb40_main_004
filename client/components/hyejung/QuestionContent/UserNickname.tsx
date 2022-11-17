type UserNicknameProps = {
  name: string;
  id: number;
};

export const UserNickname = ({ name, id }: UserNicknameProps) => {
  return (
    <button className="font-bold text-sm sm:text-[16px] space-x-1">
      💎 {name}
    </button>
  );
};
