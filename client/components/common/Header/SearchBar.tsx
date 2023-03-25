import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { useSetRecoilState } from 'recoil';
import { useRouter } from 'next/router';
import { SubmitHandler, useForm } from 'react-hook-form';

import { keywordAtom } from '@atoms/keywordAtom';

interface IFormValue {
  keyword: string;
}

export const SearchBar = () => {
  const setKeyword = useSetRecoilState(keywordAtom);
  const router = useRouter();
  const { register, handleSubmit } = useForm<IFormValue>();
  const onValid: SubmitHandler<IFormValue> = (data) => {
    if (!data.keyword.length) {
      setKeyword('');
    } else {
      setKeyword(data.keyword);
      router.push('/questions');
    }
  };
  return (
    <form className="w-full" onSubmit={handleSubmit(onValid)}>
      <input
        type="text"
        className="w-[90%] border border-solid border-font-gray rounded-full pl-2"
        {...register('keyword')}
      />
      <button type="submit">
        <FontAwesomeIcon
          icon={faMagnifyingGlass}
          className="relative -left-6"
        />
      </button>
    </form>
  );
};
