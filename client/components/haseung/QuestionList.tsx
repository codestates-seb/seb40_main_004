import { CreatedDate } from '../../components/hyejung/QuestionContent/CreatedDate';
import { QuestionTitle } from '../../components/hyejung/QuestionContent/QuestionTitle';
import { TagList } from '../../components/hyejung/QuestionContent/TagList';
import { UserNickname } from '../../components/hyejung/QuestionContent/UserNickname';

export const QuestionList = () => {
  return (
    <section className="flex flex-col w-full mt-10">
      {[...Array(9)].map((_, i) => (
        <article key={i}>
          <QuestionTitle title="리액트 쿼리 질문드립니다." />
          <div className="flex justify-start space-x-2">
            <UserNickname name="김코딩" id={1} />
            <CreatedDate createdAt="1분전" />
          </div>
          <TagList tags={['java', 'react', 'ts']} />
        </article>
      ))}
    </section>
  );
};
