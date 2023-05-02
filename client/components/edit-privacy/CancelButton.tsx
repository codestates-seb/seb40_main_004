import Link from 'next/link';

const CancelButton = () => {
  return (
    <Link href="/">
      <div className="w-full py-[6px] rounded-full bg-main-gray hover:cursor-pointer flex justify-center">
        <span>취소</span>
      </div>
    </Link>
  );
};

export default CancelButton;
