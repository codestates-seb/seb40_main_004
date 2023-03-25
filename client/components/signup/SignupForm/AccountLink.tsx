import Link from 'next/link';

type LoginTitleProps = {
  loginTitle: string;
};

const AccountLink = ({ loginTitle }: LoginTitleProps) => {
  return (
    <span className="mt-4 text-main-gray text-xs">
      이미 계정이 있으신가요?{' '}
      <Link href="/login">
        <span className="text-blue-500 cursor-pointer hover:text-blue-400 font-bold">
          {loginTitle}
        </span>
      </Link>
    </span>
  );
};

export default AccountLink;
