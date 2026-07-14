import api from './client'

/** 上传图片（博客编辑器用） */
export const uploadImage = (file: File) => {
  const form = new FormData()
  form.append('file', file)
  return api.post<{ url: string }>('/admin/upload?type=image', form)
}
