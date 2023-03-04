import React from 'react';

type EditorLabelProps = {
  htmlFor: string;
  title: string;
};

const EditorLabel = ({ htmlFor, title }: EditorLabelProps) => {
  return (
    <label htmlFor={htmlFor} className="font-bold flex">
      {title}
    </label>
  );
};

export default EditorLabel;
