import Image from 'next/image';
import Link from 'next/link';

export const Logo = () => {
  return (
    <Link href="/">
      <button className="mr-6">
        <Image src="/vercel.svg" width="100px" height="60px"></Image>
      </button>
    </Link>
  );
};
