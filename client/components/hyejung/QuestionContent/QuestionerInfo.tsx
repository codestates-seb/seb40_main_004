import { UserNickname } from './UserNickname';
import { CreatedDate } from './CreatedDate';

export const QuestionerInfo = () => {
  return (
    <section className="flex w-full justify-between">
      <article className="space-x-3 flex items-end">
        <UserNickname />
        <CreatedDate />
      </article>
    </section>
  );
};
