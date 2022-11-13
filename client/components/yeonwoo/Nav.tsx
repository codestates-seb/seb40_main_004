import Link from 'next/link';

export const Nav = () => {
  return (
    <ul className="flex gap-4">
      <li>
        <Link href="/questions">
          <button>질문/답변</button>
        </Link>
      </li>
    </ul>
  );
};
