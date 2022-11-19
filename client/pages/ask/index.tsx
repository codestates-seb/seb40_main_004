import { NextPage } from 'next';
import { Editor } from '../../components/common/Editor';
import { TagSelect } from '../../components/haseung/TagSelect';

const Ask: NextPage = () => {
  return (
    <>
      <Editor />
      <TagSelect />
    </>
  );
};

export default Ask;
