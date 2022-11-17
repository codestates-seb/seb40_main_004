import { Button } from '../../components/common/Button';
import { Intro } from '../../components/haseung/Intro';

const index = () => {
  return (
    <main className="flex justify-center text-lg">
      <article className="text-center mt-10">
        <Intro />
        <section className="px-3 text-background-gray font-semibold flex justify-center items-center bg-main-gray w-full h-20 mt-10 rounded-full">
          마지막 단계입니다! 회원님에 대해서 알려주세요!
        </section>
        <fieldset className="flex justify-start flex-col mt-10 leading-8">
          <div>
            <input type="radio" name="check" />
            <label htmlFor="개발자 취준생">개발자 취준생</label>
          </div>
          <div>
            <input type="radio" name="check" />
            <label htmlFor="현업 개발자">현업 개발자</label>
          </div>
          <div>
            <input type="radio" name="check" />
            <label htmlFor="개발에 관심있는 일반인">
              개발에 관심있는 일반인
            </label>
          </div>
        </fieldset>
        <div className="ml-2 flex justify-center items-center w-[400px] h-12 mt-10 rounded-full">
          <Button>가입하기</Button>
        </div>
      </article>
    </main>
  );
};

export default index;
